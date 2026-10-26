package com.bizpulse.app.util
import android.content.Context
import android.content.Intent
import android.content.ClipData
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import androidx.core.content.FileProvider
import com.bizpulse.app.data.db.relation.TransactionWithItems
import java.io.File
object ReportExporter {
 fun csvCell(value:String):String {val safe=if(value.trimStart().firstOrNull() in listOf('=','+','-','@'))"'"+value else value;return "\""+safe.replace("\"","\"\"")+"\""}
 fun export(context:Context,rows:List<TransactionWithItems>,pdf:Boolean,range:String):Intent {
  val dir=File(context.cacheDir,"backups").apply{mkdirs()};val file=File(dir,"BizPulse-report-${System.currentTimeMillis()}.${if(pdf)"pdf" else "csv"}")
  val lines=mutableListOf("BizPulse 2 Sales Report",range,"Revenue PHP ${rows.sumOf{it.transaction.total}}; transactions ${rows.size}")
  rows.groupBy{it.transaction.date}.toSortedMap().forEach{(date,sales)->
   lines+=date
   sales.forEach{r->val t=r.transaction;lines+="#${t.id} ${t.time} ${t.cashierName} ${t.payment} PHP ${t.total}";r.lineItems.forEach{l->lines+="${l.productName}: ${l.quantity} x PHP ${l.price}; batch ${l.batchId?:"legacy"}";lines+="Received ${l.batchReceivedAt?.let{DateUtils.stamp(it)}?:"not recorded"}; age at sale ${l.batchReceivedAt?.let{b->((t.createdAt-b)/86400000).coerceAtLeast(0).toString()+" days"}?:"not recorded"}"}}
  }
  if(pdf){PdfDocument().use{doc->var page:PdfDocument.Page?=null;var y=0;var n=0;val paint=Paint().apply{textSize=11f};lines.flatMap{it.chunked(85)}.forEach{line->if(page==null||y>790){page?.let{doc.finishPage(it)};page=doc.startPage(PdfDocument.PageInfo.Builder(595,842,++n).create());y=40};page!!.canvas.drawText(line,28f,y.toFloat(),paint);y+=17};page?.let{doc.finishPage(it)};file.outputStream().use{doc.writeTo(it)}}}
  else {
   val records=mutableListOf(listOf("Receipt","Date","Time","Cashier","Payment","Total PHP","Product","Quantity","Unit price PHP","Batch","Received","Age days at sale"))
   rows.forEach{r->val t=r.transaction;if(r.lineItems.isEmpty())records+=listOf(t.id.toString(),t.date,t.time,t.cashierName,t.payment,t.total.toString(),"Manual entry","","","","","") else r.lineItems.forEach{l->records+=listOf(t.id.toString(),t.date,t.time,t.cashierName,t.payment,t.total.toString(),l.productName,l.quantity.toString(),l.price.toString(),l.batchId?.toString()?:"",l.batchReceivedAt?.let{DateUtils.stamp(it)}?:"",l.batchReceivedAt?.let{((t.createdAt-it)/86400000).coerceAtLeast(0).toString()}?:"")}}
   file.writeText(records.joinToString("\r\n"){it.joinToString(",",transform=::csvCell)})
  }
  val uri=FileProvider.getUriForFile(context,"${context.packageName}.fileprovider",file)
  return Intent.createChooser(Intent(Intent.ACTION_SEND).apply{type=if(pdf)"application/pdf" else "text/csv";putExtra(Intent.EXTRA_STREAM,uri);clipData=ClipData.newRawUri("Sales report",uri);addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)},"Export sales report")
 }
}
