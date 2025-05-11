<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"
    xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:fo="http://www.w3.org/1999/XSL/Format">

    <xsl:template match="statistiques_equipe">
        <fo:root>
            <!-- Définition de la mise en page -->
            <fo:layout-master-set>
                <fo:simple-page-master master-name="page" page-width="21cm" page-height="29.7cm" margin="2cm">
                    <fo:region-body margin="1cm"/>
                </fo:simple-page-master>
            </fo:layout-master-set>

            <!-- Séquence de pages -->
            <fo:page-sequence master-reference="page">
                <fo:flow flow-name="xsl-region-body">
                    <fo:block font-family="Arial" font-size="12pt" font-weight="bold" text-align="center" space-after="1cm">
                        Statistiques des équipes
                    </fo:block>

                    <!-- Table pour les données des équipes -->
                    <fo:table table-layout="fixed" width="100%" border="1pt solid black" font-size="10pt">
                        <!-- En-tête du tableau -->
                        <fo:table-header>
                            <fo:table-row background-color="#f2f2f2">
                                <fo:table-cell>
                                    <fo:block>Équipe</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Matchs Joués</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Victoires</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Nuls</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Défaites</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Buts Marqués</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Buts Contre</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Diff</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Points</fo:block>
                                </fo:table-cell>
                                <fo:table-cell>
                                    <fo:block>Classement</fo:block>
                                </fo:table-cell>
                            </fo:table-row>
                        </fo:table-header>

                        <!-- Corps du tableau -->
                        <fo:table-body>
                            <xsl:for-each select="equipe">
                                <fo:table-row>
                                    <!-- Couleurs de fond en fonction du classement -->
                                    <xsl:choose>
                                        <!-- Top 4 en vert -->
                                        <xsl:when test="classement &lt;= 4">
                                            <xsl:attribute name="background-color">#d0f0c0</xsl:attribute>
                                        </xsl:when>
                                        <!-- Derniers 3 en rouge -->
                                        <xsl:when test="classement &gt;= last() - 2">
                                            <xsl:attribute name="background-color">#f4cccc</xsl:attribute>
                                        </xsl:when>
                                    </xsl:choose>

                                    <!-- Cellules de données -->
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="nom"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="matches_joues"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="victoires"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="nuls"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="defaites"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="buts_marques"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="buts_contre"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="diff"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="points"/>
                                        </fo:block>
                                    </fo:table-cell>
                                    <fo:table-cell>
                                        <fo:block>
                                            <xsl:value-of select="classement"/>
                                        </fo:block>
                                    </fo:table-cell>
                                </fo:table-row>
                            </xsl:for-each>
                        </fo:table-body>
                    </fo:table>
                </fo:flow>
            </fo:page-sequence>
        </fo:root>
    </xsl:template>
</xsl:stylesheet>
