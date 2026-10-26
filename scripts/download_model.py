import hashlib, pathlib, urllib.request
path=pathlib.Path(__file__).resolve().parents[1]/"app/src/main/assets/models/qwen2.5-0.5b-instruct-q4_k_m.gguf"
sha="74a4da8c9fdbcd15bd1f6d01d621410d31c6fc00986f5eb687824e7b93d7a9db"
def digest(p):
 h=hashlib.sha256()
 with p.open("rb") as f:
  for chunk in iter(lambda:f.read(1024*1024),b""):h.update(chunk)
 return h.hexdigest()
if path.exists() and digest(path)==sha:print("Model checksum verified")
else:
 path.parent.mkdir(parents=True,exist_ok=True);tmp=path.with_suffix(".part")
 urllib.request.urlretrieve("https://huggingface.co/Qwen/Qwen2.5-0.5B-Instruct-GGUF/resolve/9217f5db79a29953eb74d5343926648285ec7e67/qwen2.5-0.5b-instruct-q4_k_m.gguf",tmp)
 if digest(tmp)!=sha:tmp.unlink();raise RuntimeError("Model checksum mismatch")
 tmp.replace(path);print("Model downloaded and verified")
