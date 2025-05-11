<?
$file="classe.xml";
$indentation = 0;
header('Content-Type: text/html; charset=utf-8');
function startElement($parser,$name,$attribs){
    global $indentation;
    print str_repeat("&nbsp;", $indentation * 4);
    print "&lt;<font color=\"#0000cc\">$name</font>";
    if(sizeof($attribs)){
        while(list($k,$v)=each($attribs)){
        print "<font color=\"#009900\">$k</font>=\"<font
        color=\"#990000\">$v</font>\"";
        }
    }
    print "&gt;<br>";
    $indentation++;
}

function endElement($parser,$name){
    global $indentation;
    $indentation--;
    print str_repeat("&nbsp;", $indentation * 4);
    print "&lt;/<font color=\"#0000cc\">$name</font>&gt; <br>";
}

function characterData($parser,$data){
    global $indentation;
    $trimmedData = trim($data);
    if ($trimmedData) {
        print str_repeat("&nbsp;", $indentation * 4) . " $trimmedData<br>";
    }
}

$xml_parser=xml_parser_create('UTF-8');

xml_set_element_handler($xml_parser,"startElement","endElement");
xml_set_character_data_handler($xml_parser,"characterData");

if(!($fp=fopen($file,"r"))){
    die("Fichier XML: ne peut pas être ouvert !!!!");
}

while($data=fread($fp,4096)){
    if(!xml_parse($xml_parser,$data,feof($fp))){
        die("Erreur XML, ligne xml_get_current_line_number($xml_parser) !!!");
    }
} 

xml_parser_free($xml_parser);
?>