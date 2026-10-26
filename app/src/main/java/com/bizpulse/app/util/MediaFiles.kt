package com.bizpulse.app.util
import android.content.Context
import android.net.Uri
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
object MediaFiles {
 fun importImage(context:Context,uri:Uri):String {
  val bytes=context.contentResolver.openInputStream(uri)?.use{it.readBytesBounded()}?:error("Cannot read image")
  val bounds=BitmapFactory.Options().apply{inJustDecodeBounds=true};BitmapFactory.decodeByteArray(bytes,0,bytes.size,bounds)
  require(bounds.outWidth>0&&bounds.outHeight>0&&bounds.outWidth.toLong()*bounds.outHeight<=50000000){"Unsupported or oversized image"}
  val opts=BitmapFactory.Options().apply{inSampleSize=maxOf(1,maxOf(bounds.outWidth,bounds.outHeight)/1600)}
  val bitmap=BitmapFactory.decodeByteArray(bytes,0,bytes.size,opts)?:error("Invalid image")
  val dir=File(context.filesDir,"images").apply{mkdirs()};val file=File(dir,"${java.util.UUID.randomUUID()}.png")
  try{file.outputStream().use{check(bitmap.compress(Bitmap.CompressFormat.PNG,100,it))};return file.path}finally{bitmap.recycle()}
 }
 private fun java.io.InputStream.readBytesBounded():ByteArray {val out=java.io.ByteArrayOutputStream();val b=ByteArray(8192);while(true){val n=read(b);if(n<0)break;require(out.size()+n<=12*1024*1024){"Image exceeds 12 MB"};out.write(b,0,n)};return out.toByteArray()}
 fun qr(context:Context):Any?=context.getSharedPreferences("branding",0).getString("qr",null)?.let{File(it)}?:context.resources.getIdentifier("owner_qr","drawable",context.packageName).takeIf{it!=0}
}
