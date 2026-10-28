#include <jni.h>
#include <string>
#include <vector>
#include <mutex>
#include <memory>
#include <stdexcept>
#include "llama.h"
static std::mutex inference_mutex;
extern "C" JNIEXPORT jbyteArray JNICALL Java_com_bizpulse_app_util_LocalAiService_infer(JNIEnv* env,jobject,jstring path,jstring input) {
 std::lock_guard<std::mutex> guard(inference_mutex);
 const char* p=env->GetStringUTFChars(path,nullptr);std::string file(p);env->ReleaseStringUTFChars(path,p);
 const char* t=env->GetStringUTFChars(input,nullptr);std::string prompt(t);env->ReleaseStringUTFChars(input,t);
 try {
  llama_backend_init();auto mp=llama_model_default_params();mp.n_gpu_layers=0;
  std::unique_ptr<llama_model,decltype(&llama_model_free)> model(llama_model_load_from_file(file.c_str(),mp),llama_model_free);
  if(!model)throw std::runtime_error("Cannot load offline model");
  const auto* vocab=llama_model_get_vocab(model.get());
  int n=-llama_tokenize(vocab,prompt.c_str(),prompt.size(),nullptr,0,true,true);
  if(n<=0 || n>1500)throw std::runtime_error("AI input too long");
  std::vector<llama_token> tokens(n);llama_tokenize(vocab,prompt.c_str(),prompt.size(),tokens.data(),n,true,true);
  auto cp=llama_context_default_params();cp.n_ctx=2048;cp.n_batch=1536;cp.n_threads=4;cp.n_threads_batch=4;
  std::unique_ptr<llama_context,decltype(&llama_free)> ctx(llama_init_from_model(model.get(),cp),llama_free);
  if(!ctx)throw std::runtime_error("Not enough memory for offline AI");
  std::unique_ptr<llama_sampler,decltype(&llama_sampler_free)> sampler(llama_sampler_init_greedy(),llama_sampler_free);
  auto batch=llama_batch_get_one(tokens.data(),tokens.size());std::string output;llama_token next;
  for(int i=0;i<180;i++){
   if(llama_decode(ctx.get(),batch)!=0)throw std::runtime_error("AI decoding failed");
   next=llama_sampler_sample(sampler.get(),ctx.get(),-1);if(llama_vocab_is_eog(vocab,next))break;
   char small[256];int bytes=llama_token_to_piece(vocab,next,small,sizeof(small),0,true);
   if(bytes>0)output.append(small,bytes);
   batch=llama_batch_get_one(&next,1);
  }
  auto result=env->NewByteArray(output.size());env->SetByteArrayRegion(result,0,output.size(),reinterpret_cast<const jbyte*>(output.data()));return result;
 }catch(const std::exception& e){env->ThrowNew(env->FindClass("java/lang/IllegalStateException"),e.what());return nullptr;}
}
