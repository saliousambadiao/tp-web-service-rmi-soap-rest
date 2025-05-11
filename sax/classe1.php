<?php
$xml_parser = xml_parser_create();
$f = "classe.xml";
$file1 = implode("", file($f));
xml_parser_set_option($xml_parser, XML_OPTION_CASE_FOLDING, 0);
xml_parser_set_option($xml_parser, XML_OPTION_SKIP_WHITE, 1);
xml_parse_into_struct($xml_parser, $file1, $struct, $ind);
xml_parser_free($xml_parser);

// Fonction pour afficher avec indentation
function displayIndentedXML($struct) {
    $indent = 0;
    foreach ($struct as $tag) {
        if ($tag['type'] == 'open') {
            // Balise ouvrante
            echo str_repeat("&nbsp;", $indent * 4) . "&lt;<font color=\"#0000cc\">" . $tag['tag'] . "</font>";
            if (isset($tag['attributes'])) {
                foreach ($tag['attributes'] as $key => $value) {
                    echo " <font color=\"#009900\">$key</font>=\"<font color=\"#990000\">$value</font>\"";
                }
            }
            echo "&gt;<br>";
            $indent++;
        } elseif ($tag['type'] == 'close') {
            // Balise fermante
            $indent--;
            echo str_repeat("&nbsp;", $indent * 4) . "&lt;/<font color=\"#0000cc\">" . $tag['tag'] . "</font>&gt;<br>";
        } elseif ($tag['type'] == 'complete') {
            // Balise complète
            echo str_repeat("&nbsp;", $indent * 4) . "&lt;<font color=\"#0000cc\">" . $tag['tag'] . "</font>";
            if (isset($tag['attributes'])) {
                foreach ($tag['attributes'] as $key => $value) {
                    echo " <font color=\"#009900\">$key</font>=\"<font color=\"#990000\">$value</font>\"";
                }
            }
            echo "&gt;" . htmlspecialchars(isset($tag['value']) ? $tag['value'] : "") . "&lt;/<font color=\"#0000cc\">" . $tag['tag'] . "</font>&gt;<br>";
        }
    }
}

// Affichage avec indentation
displayIndentedXML($struct);
?>
