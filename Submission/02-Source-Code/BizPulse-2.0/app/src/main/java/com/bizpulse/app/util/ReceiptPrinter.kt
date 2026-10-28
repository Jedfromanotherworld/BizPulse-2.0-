package com.bizpulse.app.util
import android.content.Context
import android.graphics.Paint
import android.graphics.pdf.PdfDocument
import android.os.*
import android.print.*
import com.bizpulse.app.data.db.relation.TransactionWithItems
object ReceiptPrinter {
    fun lines(receipt:TransactionWithItems):List<String> {
        val t=receipt.transaction; val subtotal=rounded(receipt.lineItems.sumOf { it.quantity*it.price })
        return listOf("BIZPULSE","Sales receipt #${t.id}","${t.date}  ${t.time}","Cashier: ${t.cashierName}","") +
            receipt.lineItems.flatMap { listOf(it.productName,"${it.quantity} x ${money(it.price)} = ${money(it.quantity*it.price)}") } +
            listOf("","Subtotal: ${money(subtotal)}","VAT (12%): ${money(t.total-subtotal)}","TOTAL: ${money(t.total)}","Payment: ${t.payment}","Tendered: ${money(t.tendered)}","Change: ${money((t.tendered-t.total).coerceAtLeast(0.0))}","","Thank you for shopping!","Academic demo receipt")
    }
    fun print(context:Context,receipt:TransactionWithItems) {
        val rows=lines(receipt).flatMap { text -> if(text.isEmpty()) listOf("") else text.chunked(65) }
        val chunks=rows.chunked(42)
        (context.getSystemService(Context.PRINT_SERVICE) as PrintManager).print("BizPulse-${receipt.transaction.id}",object:PrintDocumentAdapter() {
            override fun onLayout(oldAttributes:PrintAttributes?,newAttributes:PrintAttributes?,cancellationSignal:CancellationSignal?,callback:LayoutResultCallback,extras:Bundle?) {
                if(cancellationSignal?.isCanceled==true) callback.onLayoutCancelled()
                else callback.onLayoutFinished(PrintDocumentInfo.Builder("BizPulse-${receipt.transaction.id}.pdf").setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT).setPageCount(chunks.size).build(),true)
            }
            override fun onWrite(pages:Array<out PageRange>,destination:ParcelFileDescriptor,cancellationSignal:CancellationSignal,callback:WriteResultCallback) {
                val document=PdfDocument()
                try {
                    val written=mutableListOf<PageRange>()
                    chunks.forEachIndexed { index,chunk ->
                        if(cancellationSignal.isCanceled) {callback.onWriteCancelled();return}
                        if(pages.any {index in it.start..it.end}) {
                            val page=document.startPage(PdfDocument.PageInfo.Builder(595,842,index+1).create())
                            val paint=Paint(Paint.ANTI_ALIAS_FLAG).apply {textSize=12f;color=android.graphics.Color.BLACK}
                            chunk.forEachIndexed { i,text -> page.canvas.drawText(text,40f,50f+i*17f,paint) }
                            page.canvas.drawText("Page ${index+1} / ${chunks.size}",40f,800f,paint)
                            document.finishPage(page);written.add(PageRange(index,index))
                        }
                    }
                    ParcelFileDescriptor.AutoCloseOutputStream(destination).use {document.writeTo(it)}
                    callback.onWriteFinished(written.toTypedArray())
                } catch(e:Exception) {callback.onWriteFailed(e.message)} finally {document.close()}
            }
        },PrintAttributes.Builder().setMediaSize(PrintAttributes.MediaSize.ISO_A4).build())
    }
}
