package com.bizpulse.app.util
import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.io.File
@Singleton class LocalAiService @Inject constructor(@ApplicationContext private val context:Context) {
 private external fun infer(path:String,prompt:String):ByteArray
 suspend fun generateInsight(data:String)=generate(data,"Write exactly three short bullet points: Trend, Risk, Recommendation.")
 suspend fun generateDailySummary(data:String)=generate(data,"Give a short daily store summary and one practical next step.")
 suspend fun generateAppHealth(data:String)=generate(data,"Explain the supplied stock-health status in one sentence. Do not diagnose software or device health.")
 private suspend fun generate(data:String,instruction:String):String=withContext(Dispatchers.IO){mutex.withLock {
  try {System.loadLibrary("bizpulse_ai")}catch(e:UnsatisfiedLinkError){error("Offline AI is unavailable for this device architecture")}
  val file=File(context.filesDir,"models/qwen2.5-0.5b-instruct-q4_k_m.gguf")
  if(!file.exists()){file.parentFile!!.mkdirs();val tmp=File(file.parentFile,"model.tmp");try{context.assets.open("models/qwen2.5-0.5b-instruct-q4_k_m.gguf").use{input->tmp.outputStream().use{input.copyTo(it)}};check(tmp.renameTo(file)){"Cannot prepare model"}}finally{tmp.delete()}}
  val prompt="<|im_start|>system\nYou explain store statistics in plain English. Use only supplied numeric facts. Never invent sales, causes, forecasts or payment confirmation. $instruction<|im_end|>\n<|im_start|>user\n$data<|im_end|>\n<|im_start|>assistant\n"
  infer(file.absolutePath,prompt).toString(Charsets.UTF_8).trim().ifBlank{"No summary generated. Try again."}
 }}
 companion object{private val mutex=Mutex()}
}
