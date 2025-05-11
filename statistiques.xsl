<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:template match="statistiques_equipe">
        <html>
            <head>
                <title>Statistiques des équipes</title>
                <style>
                    table { width: 80%; border-collapse: collapse; }
                    th, td { padding: 10px; border: 1px solid black; text-align: center; }
                    th { background-color: #f2f2f2; }
                    .top-ranked { background-color: #d0f0c0; } /* Vert clair */
                    .bottom-ranked { background-color: #f0c0c0; } /* Rouge clair */
                </style>
            </head>
            <body>
                <h2>Statistiques des équipes</h2>
                <table>
                    <tr>
                        <th>Équipe</th>
                        <th>Classement</th>
                        <th>Matchs Joués</th>
                        <th>Victoires</th>
                        <th>Nuls</th>
                        <th>Défaites</th>
                        <th>Buts Marqués</th>
                        <th>Buts Contre</th>
                        <th>Différence de Buts</th>
                        <th>Points</th>
                    </tr>
                    <xsl:for-each select="equipe">
                        <tr>
                            <xsl:choose>
                                <xsl:when test="classement &lt;= 4">
                                    <xsl:attribute name="class">top-ranked</xsl:attribute>
                                </xsl:when>
                                <xsl:when test="classement &gt;= 18">
                                    <xsl:attribute name="class">bottom-ranked</xsl:attribute>
                                </xsl:when>
                            </xsl:choose>
                            <td>
                                <xsl:value-of select="nom"/>
                            </td>
                            <td>
                                <xsl:value-of select="classement"/>
                            </td>
                            <td>
                                <xsl:value-of select="matchs_joues"/>
                            </td>
                            <td>
                                <xsl:value-of select="victoires"/>
                            </td>
                            <td>
                                <xsl:value-of select="nuls"/>
                            </td>
                            <td>
                                <xsl:value-of select="defaites"/>
                            </td>
                            <td>
                                <xsl:value-of select="buts_marques"/>
                            </td>
                            <td>
                                <xsl:value-of select="buts_contre"/>
                            </td>
                            <td>
                                <xsl:value-of select="difference_buts"/>
                            </td>
                            <td>
                                <xsl:value-of select="points"/>
                            </td>
                        </tr>
                    </xsl:for-each>
                </table>
            </body>
        </html>
    </xsl:template>
</xsl:stylesheet>
