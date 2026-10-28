package com.bizpulse.app.ui.components
import androidx.compose.runtime.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight
import com.bizpulse.app.ui.theme.*
import com.bizpulse.app.util.*
@Composable fun Panel(content:@Composable ColumnScope.()->Unit) {
    Card(Modifier.fillMaxWidth(),shape=CardShape,colors=CardDefaults.cardColors(containerColor=MaterialTheme.colorScheme.surface)) { Column(Modifier.padding(16.dp),verticalArrangement=Arrangement.spacedBy(8.dp),content=content) }
}
@Composable fun Page(content:@Composable ColumnScope.()->Unit) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp),verticalArrangement=Arrangement.spacedBy(16.dp),content=content)
}
@Composable fun Tabs(options:List<String>,selected:String,onSelect:(String)->Unit) {
    Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        options.forEach { name -> FilterChip(selected=name==selected,onClick={ onSelect(name) },label={ Text(name) }) }
    }
}
@Composable fun Field(label:String,value:String,onValue:(String)->Unit,numeric:Boolean=false,enabled:Boolean=true,password:Boolean=false,visible:Boolean=false,onToggle:()->Unit={}) {
    OutlinedTextField(value=value,onValueChange=onValue,label={ Text(label) },singleLine=true,enabled=enabled,shape=InputShape,modifier=Modifier.fillMaxWidth(),
        visualTransformation=if(password && !visible) androidx.compose.ui.text.input.PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions=androidx.compose.foundation.text.KeyboardOptions(keyboardType=if(password) androidx.compose.ui.text.input.KeyboardType.Password else if(numeric) androidx.compose.ui.text.input.KeyboardType.Decimal else androidx.compose.ui.text.input.KeyboardType.Text),
        trailingIcon=if(password) { { TextButton(onClick=onToggle) { Text(if(visible) "Hide" else "Show") } } } else null)
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable fun Choice(label:String,options:List<Pair<String,String>>,value:String,vm:com.bizpulse.app.ui.screens.ScreenViewModel,onValue:(String)->Unit) {
    val ui by vm.ui.collectAsState()
    if(label=="Role") {
        Column {
            Text(label,style=MaterialTheme.typography.labelSmall)
            Row(Modifier.horizontalScroll(rememberScrollState()),horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                options.forEach { (key,text) -> FilterChip(selected=key==value,onClick={onValue(key)},label={Text(text)}) }
            }
        }
    } else {
        val expanded=ui.selection=="dropdown-$label"
        ExposedDropdownMenuBox(expanded=expanded,onExpandedChange={ vm.select(if(it) "dropdown-$label" else "") }) {
            OutlinedTextField(value=options.find { it.first==value }?.second ?: "Choose $label",onValueChange={},readOnly=true,
                label={Text(label)},shape=InputShape,modifier=Modifier.menuAnchor().fillMaxWidth(),
                trailingIcon={ExposedDropdownMenuDefaults.TrailingIcon(expanded=expanded)})
            ExposedDropdownMenu(expanded=expanded,onDismissRequest={vm.select("")}) {
                options.forEach { (key,text) -> DropdownMenuItem(text={Text(text)},onClick={onValue(key);vm.select("")}) }
            }
        }
    }
}
@Composable fun Notice(ui:com.bizpulse.app.ui.screens.ScreenUi,onDismiss:()->Unit) {
    if(ui.busy) LinearProgressIndicator(Modifier.fillMaxWidth())
    ui.message?.let { text -> Surface(color=MaterialTheme.colorScheme.secondaryContainer,shape=InputShape) { Column(Modifier.fillMaxWidth().padding(12.dp)) { Text(text); TextButton(onClick=onDismiss) { Text("Dismiss") } } } }
}
@Composable fun FormDialog(title:String,ui:com.bizpulse.app.ui.screens.ScreenUi,onDismiss:()->Unit,onSave:()->Unit,content:@Composable ColumnScope.()->Unit) {
    AlertDialog(onDismissRequest={ if(!ui.busy) onDismiss() },shape=ModalShape,title={ Text(title) },text={ Column(Modifier.verticalScroll(rememberScrollState()),verticalArrangement=Arrangement.spacedBy(8.dp)) { content(); if(ui.busy) LinearProgressIndicator(Modifier.fillMaxWidth()); ui.message?.let { Text(it,color=MaterialTheme.colorScheme.error) } } },confirmButton={ TextButton(onClick=onSave,enabled=!ui.busy) { Text(if(ui.busy) "Saving…" else "Save") } },dismissButton={ TextButton(onClick=onDismiss,enabled=!ui.busy) { Text("Cancel") } })
}
@Composable fun ScreenHeader(title:String,subtitle:String="") { Column(verticalArrangement=Arrangement.spacedBy(4.dp)) { Text(title,style=MaterialTheme.typography.headlineLarge); if(subtitle.isNotBlank()) Text(subtitle,color=MaterialTheme.colorScheme.onSurfaceVariant) } }
