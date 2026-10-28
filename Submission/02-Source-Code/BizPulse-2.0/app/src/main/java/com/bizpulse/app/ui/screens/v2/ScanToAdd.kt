package com.bizpulse.app.ui.screens.v2
import android.Manifest
import android.graphics.Bitmap
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import com.journeyapps.barcodescanner.*
import com.google.zxing.BarcodeFormat
import com.google.zxing.oned.Code128Writer
import com.bizpulse.app.ui.components.*
import com.bizpulse.app.ui.screens.inventory.InventoryViewModel
@Composable fun ScanToAdd(vm:InventoryViewModel){
 var open by remember{mutableStateOf(false)};var code by remember{mutableStateOf("")};val context=LocalContext.current
 val scanner=rememberLauncherForActivityResult(ScanContract()){it.contents?.let{result->code=result}}
 fun scan(){scanner.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.ALL_CODE_TYPES).setOrientationLocked(false).setBeepEnabled(false))}
 val permission=rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()){if(it)scan()else vm.message("Camera permission denied; type the barcode instead")}
 OutlineButton("Scan to Add Product"){code="";open=true}
 if(open){
  val valid=code.length in 1..80 && code.all{it.code in 32..126}
  val bitmap=remember(code){if(valid)runCatching{val matrix=Code128Writer().encode(code,BarcodeFormat.CODE_128,1000,260);Bitmap.createBitmap(matrix.width,matrix.height,Bitmap.Config.ARGB_8888).apply{for(y in 0 until matrix.height)for(x in 0 until matrix.width)setPixel(x,y,if(matrix[x,y])android.graphics.Color.BLACK else android.graphics.Color.WHITE)}}.getOrNull()else null}
  AlertDialog(onDismissRequest={open=false},title={Text("Scan or enter a barcode")},text={Column{
   Field("Barcode",code,{code=it})
   Row {
    TextButton(onClick={if(ContextCompat.checkSelfPermission(context,Manifest.permission.CAMERA)==PackageManager.PERMISSION_GRANTED)scan()else permission.launch(Manifest.permission.CAMERA)}){Text("Camera")}
    TextButton(onClick={code="BP"+System.currentTimeMillis()}){Text("Simulate")}
   }
   if(bitmap!=null){Image(bitmap.asImageBitmap(),"Code128 barcode",Modifier.fillMaxWidth().height(100.dp));Text(code)}else Text("Enter 1–80 printable ASCII characters")
   Text("Existing barcode: edit product. New barcode: add product.")
  }},confirmButton={TextButton(enabled=bitmap!=null,onClick={open=false;vm.prefill(code)}){Text("Continue")}},dismissButton={TextButton(onClick={open=false}){Text("Cancel")}})
 }
}
