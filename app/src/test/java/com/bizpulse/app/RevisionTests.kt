package com.bizpulse.app
import com.bizpulse.app.data.db.entity.BatchEntity
import com.bizpulse.app.util.Fifo
import com.bizpulse.app.util.ReportExporter
import com.bizpulse.app.ui.screens.v2.shiftDuration
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.oned.Code128Writer
import org.junit.Assert.*
import org.junit.Test
class RevisionTests {
 private fun batch(id:Long,time:Long,n:Int)=BatchEntity(id,1,"Supplier",time,n,10.0,n)
 @Test fun oldestStockIsAllocatedAcrossBatches(){val result=Fifo.allocate(listOf(batch(3,300,5),batch(2,200,3),batch(1,100,2)),4);assertEquals(listOf(1L,2L),result.map{it.first.id});assertEquals(listOf(2,2),result.map{it.second})}
 @Test fun stableOrderForSameTimestamp(){assertEquals(1L,Fifo.allocate(listOf(batch(2,100,2),batch(1,100,2)),1).first().first.id)}
 @Test(expected=IllegalArgumentException::class) fun insufficientStockIsRejected(){Fifo.allocate(listOf(batch(1,100,2)),3)}
 @Test(expected=IllegalArgumentException::class) fun zeroQuantityIsRejected(){Fifo.allocate(emptyList(),0)}
 @Test fun depletedBatchesAreSkipped(){assertEquals(2L,Fifo.allocate(listOf(batch(1,100,0),batch(2,200,1)),1).first().first.id)}
 @Test fun csvEscapesQuotesAndFormulas(){assertEquals("\"'=1+1\"",ReportExporter.csvCell("=1+1"));assertEquals("\"' =1+1\"",ReportExporter.csvCell(" =1+1"));assertEquals("\"A,\"\"B\"\"\"",ReportExporter.csvCell("A,\"B\""))}
 @Test fun durationHandlesMidnightAndInvalidClock(){assertEquals("25:01:01",shiftDuration(1000,90062000));assertEquals("00:00:00",shiftDuration(2000,1000))}
 @Test fun typedBarcodesAreScannable(){
  listOf("4801234567890","BP-001","987654321").forEach{value->
   val m=Code128Writer().encode(value,BarcodeFormat.CODE_128,1000,260)
   val pixels=IntArray(m.width*m.height){i->if(m[i%m.width,i/m.width])0xff000000.toInt() else 0xffffffff.toInt()}
   assertEquals(value,MultiFormatReader().decode(BinaryBitmap(HybridBinarizer(RGBLuminanceSource(m.width,m.height,pixels)))).text)
  }
 }
}
