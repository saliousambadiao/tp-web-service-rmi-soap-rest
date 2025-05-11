<?
$file="classe.xml";
function startElement($parser,$name,$attribs){
print "&lt;<font color=\"#0000cc\">$name</font>";
if(sizeof($attribs)){
while(list($k,$v)=each($attribs)){
print "<font color=\"#009900\">$k</font>=\"<font
color=\"#990000\">$v</font>\"";
}} print "&gt;";
}
function endElement($parser,$name){
print "&lt;<font color=\"#0000cc\">$name</font>&gt; <br>";
}
function characterData($parser,$data){
print "$data";
}
$xml_parser=xml_parser_create();
xml_set_element_handler($xml_parser,"startElement","endElement");
xml_set_character_data_handler($xml_parser,"characterData");
if(!($fp=fopen($file,"r"))){
die("Fichier XML: ne peut pas être ouvert !!!!");
} while($data=fread($fp,4096)){
if(!xml_parse($xml_parser,$data,feof($fp))){
die("Erreur XML, ligne
xml_get_current_line_number($xml_parser) !!!");
}} xml_parser_free($xml_parser);
?>