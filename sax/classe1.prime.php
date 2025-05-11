<?
$xml_parser=xml_parser_create();
$f="classe.xml";
$file1=implode(file($f),"");
xml_parser_set_option($xml_parser,XML_OPTION_CASE_FOLDING,0);
xml_parser_set_option($xml_parser,XML_OPTION_SKIP_WHITE,1);
xml_parse_into_struct($xml_parser,$file1,$struct,$ind);
print "Les indices : <br><br>";
print_r($ind);
print "<br><br> Les structures : <br><br>";
print_r($struct);
xml_parser_free($xml_parser);
?>