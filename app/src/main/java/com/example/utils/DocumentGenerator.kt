package com.example.utils

import com.example.data.model.Nazhir
import com.example.data.model.WakafLand
import java.text.SimpleDateFormat
import java.util.*

object DocumentGenerator {

    private val dateFormatter = SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID"))

    fun getTodayString(): String = dateFormatter.format(Date())

    fun getDocumentTitle(docType: Int): String {
        return when (docType) {
            1 -> "Surat Pernyataan Wakaf (SPW)"
            2 -> "Surat Permohonan Pendaftaran Nazhir ke KUA"
            3 -> "Rencana Program Kerja Tanah Wakaf"
            4 -> "Daftar Kekayaan Tanah Wakaf"
            5 -> "Surat Pernyataan Bersedia Diaudit"
            6 -> "Surat Kuasa Balik Nama BPN"
            7 -> "Surat Pernyataan Bebas Sengketa"
            8 -> "Surat Persetujuan Suami/Istri Wakif"
            9 -> "Piagam Penghargaan Wakif"
            else -> "Dokumen Wakaf"
        }
    }

    // Returns HTML formatted document suitable for rendering in WebView or printing
    fun generateHtml(docType: Int, land: WakafLand, nazhirs: List<Nazhir>): String {
        val n1 = nazhirs.getOrNull(0) ?: Nazhir(name = "(Nama Ketua Nazhir)", identityNo = "-", age = 0, profession = "-", address = "-", kecamatan = "-", position = "Ketua")
        val n2 = nazhirs.getOrNull(1) ?: Nazhir(name = "(Nama Sekretaris Nazhir)", identityNo = "-", age = 0, profession = "-", address = "-", kecamatan = "-", position = "Sekretaris")
        val n3 = nazhirs.getOrNull(2) ?: Nazhir(name = "(Nama Bendahara Nazhir)", identityNo = "-", age = 0, profession = "-", address = "-", kecamatan = "-", position = "Bendahara")

        val creationDate = dateFormatter.format(Date(land.createdAt))
        val certStatus = if (land.isCertified) "Sertipikat Hak Milik" else "Letter C / Belum Bersertipikat"

        val bodyContent = when (docType) {
            1 -> { // Surat Pernyataan Wakaf
                """
                <div class="header">
                    <h2>SURAT PERNYATAAN WAKAF (SPW)</h2>
                </div>
                <p>Yang bertanda tangan di bawah ini (selaku WAKIF):</p>
                <table class="info-table">
                    <tr><td width="30%"><strong>Nama Lengkap</strong></td><td>: ${land.wakifName}</td></tr>
                    <tr><td><strong>NIK (KTP)</strong></td><td>: ${land.wakifIdentityNo}</td></tr>
                    <tr><td><strong>Umur</strong></td><td>: ${land.wakifAge} Tahun</td></tr>
                    <tr><td><strong>Pekerjaan</strong></td><td>: ${land.wakifProfession}</td></tr>
                    <tr><td><strong>Alamat</strong></td><td>: ${land.wakifAddress}</td></tr>
                </table>
                
                <p>Dengan kesadaran penuh tanpa paksaan dari pihak manapun, menyatakan dengan sesungguhnya bahwa saya dengan ini <strong>MEWAKAFKAN</strong> sebidang tanah milik saya:</p>
                <table class="info-table">
                    <tr><td width="30%"><strong>Luas Tanah</strong></td><td>: ${land.landArea} m²</td></tr>
                    <tr><td><strong>Status Kepemilikan</strong></td><td>: $certStatus</td></tr>
                    <tr><td><strong>Nomor Dokumen</strong></td><td>: ${land.certificateOrLetterCNo}</td></tr>
                    <tr><td><strong>Lokasi Tanah</strong></td><td>: Kel/Desa ${land.landDesa}, Kec. ${land.landKecamatan}</td></tr>
                    <tr><td><strong>Koordinat (GPS)</strong></td><td>: ${land.coordinateLat}, ${land.coordinateLng}</td></tr>
                    <tr><td><strong>Peruntukan Wakaf</strong></td><td>: ${land.purpose}</td></tr>
                </table>
                
                <p>Adapun batas-batas tanah wakaf tersebut adalah sebagai berikut:</p>
                <table class="info-table" style="margin-left: 20px;">
                    <tr><td width="30%"><strong>Batas Utara</strong></td><td>: ${land.boundaryNorth}</td></tr>
                    <tr><td><strong>Batas Selatan</strong></td><td>: ${land.boundarySouth}</td></tr>
                    <tr><td><strong>Batas Timur</strong></td><td>: ${land.boundaryEast}</td></tr>
                    <tr><td><strong>Batas Barat</strong></td><td>: ${land.boundaryWest}</td></tr>
                </table>
                
                <p>Saya menyatakan pula dengan sebenar-benarnya bahwa tanah wakaf tersebut di atas dalam keadaan <strong>TIDAK DALAM SENGKETA</strong> hukum dengan pihak mana pun dan <strong>TIDAK SEDANG DIJAMINKAN KE BANK</strong> atau pihak pembiayaan lainnya sebagai agunan pinjaman.</p>
                
                <p>Demikian surat pernyataan wakaf ini dibuat dengan sebenar-benarnya demi kemaslahatan umat dan kelancaran pencatatan administrasi wakaf.</p>
                
                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>
                
                <table class="sign-table">
                    <tr>
                        <td width="50%">Pihak yang Mewakafkan (WAKIF),</td>
                        <td width="50%">Menerima Wakaf (NAZHIR KETUA),</td>
                    </tr>
                    <tr class="spacer"><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${land.wakifName}</strong></td>
                        <td class="sign-name"><strong>${n1.name}</strong></td>
                    </tr>
                </table>

                <br><br>
                <h4 style="text-align: center; margin-bottom: 5px;">Para Saksi:</h4>
                <table class="sign-table">
                    <tr>
                        <td width="50%">Saksi I,</td>
                        <td width="50%">Saksi II,</td>
                    </tr>
                    <tr class="spacer"><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${land.witness1Name}</strong></td>
                        <td class="sign-name"><strong>${land.witness2Name}</strong></td>
                    </tr>
                </table>

                <br><br>
                <hr>
                <h4 style="text-align: center; margin-bottom: 5px; margin-top: 5px;">Mengetahui / Mengesahkan:</h4>
                <table class="sign-table">
                    <tr>
                        <td width="50%">Kepala Desa / Lurah Kel. ${land.landDesa},</td>
                        <td width="50%">Camat Kecamatan ${land.landKecamatan},</td>
                    </tr>
                    <tr class="spacer"><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${land.villageHeadName}</strong></td>
                        <td class="sign-name"><strong>${land.camatName}</strong></td>
                    </tr>
                </table>
                """.trimIndent()
            }
            2 -> { // Surat Pendaftaran Nazhir ke KUA
                """
                <div class="header">
                    <h2>SURAT PERMOHONAN PENDAFTARAN NAZHIR</h2>
                </div>
                <div class="kua-header" style="margin-bottom: 25px;">
                    <p>Hal: Permohonan Pendaftaran Nazhir Tanah Wakaf <br>
                    Lampiran: 1 Berkas Dokumen Pernyataan Wakaf</p>
                    <p style="margin-top: 15px;">Kepada Yth.<br>
                    <strong>Kepala Kantor Urusan Agama (KUA) Kecamatan ${land.landKecamatan}</strong><br>
                    Di Tempat</p>
                </div>

                <p>Assalamu'alaikum Wr. Wb.</p>
                <p>Dengan hormat, demi terwujudnya tertib administrasi perwasiatan dan perwakafan di tingkat kecamatan, kami selaku para Nazhir yang ditunjuk oleh Wakif mengajukan permohonan pendaftaran kepengurusan Nazhir atas sebidang tanah wakaf yang telah diserahkan oleh:</p>
                
                <table class="info-table" style="margin-left: 15px;">
                    <tr><td width="30%"><strong>Nama Wakif</strong></td><td>: ${land.wakifName}</td></tr>
                    <tr><td><strong>Alamat Wakif</strong></td><td>: ${land.wakifAddress}</td></tr>
                </table>

                <p>Dengan objek tanah wakaf sebagai berikut:</p>
                <table class="info-table" style="margin-left: 15px;">
                    <tr><td width="30%"><strong>Luas Objek</strong></td><td>: ${land.landArea} m²</td></tr>
                    <tr><td><strong>Dokumen Legalitas</strong></td><td>: $certStatus No. ${land.certificateOrLetterCNo}</td></tr>
                    <tr><td><strong>Lokasi Fisik</strong></td><td>: Desa/Lurah ${land.landDesa}, Kec. ${land.landKecamatan}</td></tr>
                    <tr><td><strong>Rencana Peruntukan</strong></td><td>: ${land.purpose}</td></tr>
                </table>

                <p>Adapun susunan kepengurusan lengkap 3 (tiga) orang Nazhir yang akan mengelola tanah wakaf tersebut adalah:</p>
                
                <div class="nazhir-list-block">
                    <p><strong>1. KETUA NAZHIR:</strong></p>
                    <table class="info-table" style="margin-left: 20px; font-size: 13px;">
                        <tr><td width="30%">Nama Lengkap</td><td>: ${n1.name}</td></tr>
                        <tr><td>NIK (KTP)</td><td>: ${n1.identityNo}</td></tr>
                        <tr><td>Umur / Pekerjaan</td><td>: ${n1.age} Tahun / ${n1.profession}</td></tr>
                        <tr><td>Alamat Rumah</td><td>: ${n1.address}</td></tr>
                    </table>

                    <p style="margin-top: 10px;"><strong>2. SEKRETARIS NAZHIR:</strong></p>
                    <table class="info-table" style="margin-left: 20px; font-size: 13px;">
                        <tr><td width="30%">Nama Lengkap</td><td>: ${n2.name}</td></tr>
                        <tr><td>NIK (KTP)</td><td>: ${n2.identityNo}</td></tr>
                        <tr><td>Umur / Pekerjaan</td><td>: ${n2.age} Tahun / ${n2.profession}</td></tr>
                        <tr><td>Alamat Rumah</td><td>: ${n2.address}</td></tr>
                    </table>

                    <p style="margin-top: 10px;"><strong>3. BENDAHARA NAZHIR:</strong></p>
                    <table class="info-table" style="margin-left: 20px; font-size: 13px;">
                        <tr><td width="30%">Nama Lengkap</td><td>: ${n3.name}</td></tr>
                        <tr><td>NIK (KTP)</td><td>: ${n3.identityNo}</td></tr>
                        <tr><td>Umur / Pekerjaan</td><td>: ${n3.age} Tahun / ${n3.profession}</td></tr>
                        <tr><td>Alamat Rumah</td><td>: ${n3.address}</td></tr>
                    </table>
                </div>

                <p style="margin-top: 15px;">Kami memohon agar Kepala KUA Kecamatan ${land.landKecamatan} dapat menerbitkan Surat Pengesahan Nazhir agar pengelolaan dan pensertifikatan tanah tersebut di Badan Pertanahan Nasional dapat berjalan lancar.</p>
                <p>Demikian permohonan ini kami sampaikan. Atas bantuan dan kerja sama Bapak, kami ucapkan terima kasih.</p>
                <p>Wassalamu'alaikum Wr. Wb.</p>

                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>

                <h4 style="text-align: center; margin-top: 15px; margin-bottom: 5px;">Hormat Kami, Pengurus Nazhir Walau:</h4>
                <table class="sign-table-three">
                    <tr>
                        <td width="33%">Ketua Nazhir,</td>
                        <td width="33%">Sekretaris Nazhir,</td>
                        <td width="33%">Bendahara Nazhir,</td>
                    </tr>
                    <tr class="spacer-small"><td></td><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${n1.name}</strong></td>
                        <td class="sign-name"><strong>${n2.name}</strong></td>
                        <td class="sign-name"><strong>${n3.name}</strong></td>
                    </tr>
                </table>
                """.trimIndent()
            }
            3 -> { // Rencana Program Kerja Tanah Wakaf
                """
                <div class="header">
                    <h2>RENCANA PROGRAM KERJA TANAH WAKAF</h2>
                    <h3 style="text-align: center; font-weight: normal; margin-top: -10px;">PENGELOLAAN DAN PENGEMBANGAN PRODUKTIF DAN SOSIAL</h3>
                </div>

                <p>Dalam rangka menjaga keberlangsungan aset wakaf dan pemanfaatan yang optimal bagi umat, Nazhir menetapkan program kerja pengelolaan tanah wakaf milik Wakif:</p>
                
                <table class="info-table" style="margin-bottom: 20px;">
                    <tr><td width="30%"><strong>Wakif Asal</strong></td><td>: ${land.wakifName}</td></tr>
                    <tr><td><strong>Alamat Objek</strong></td><td>: Desa/Kel. ${land.landDesa}, Kec. ${land.landKecamatan}</td></tr>
                    <tr><td><strong>Identifikasi Objek</strong></td><td>: Luas ${land.landArea} m² ($certStatus No. ${land.certificateOrLetterCNo})</td></tr>
                    <tr><td><strong>Peruntukan Utama</strong></td><td>: ${land.purpose}</td></tr>
                </table>

                <hr>

                <h3>1. PROGRAM JANGKA PENDEK (0 - 1 Tahun)</h3>
                <div class="program-block">
                    <p>${land.programShortTerm}</p>
                </div>

                <h3>2. PROGRAM JANGKA MENENGAH (1 - 5 Tahun)</h3>
                <div class="program-block">
                    <p>${land.programMediumTerm}</p>
                </div>

                <h3>3. PROGRAM JANGKA PANJANG (Di atas 5 Tahun)</h3>
                <div class="program-block">
                    <p>${land.programLongTerm}</p>
                </div>

                <p style="margin-top: 20px;">Program kerja ini disusun dengan mengutamakan prinsip kemandirian dakwah, transparansi pelaporan aset, serta keberpihakan atas hak-hak sosial-keumatan (Mauquf 'Alaih).</p>

                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>

                <table class="sign-table">
                    <tr>
                        <td width="50%">Pihak Wakif,</td>
                        <td width="50%">Ketua Nazhir Pengelola,</td>
                    </tr>
                    <tr class="spacer"><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${land.wakifName}</strong></td>
                        <td class="sign-name"><strong>${n1.name}</strong></td>
                    </tr>
                </table>
                """.trimIndent()
            }
            4 -> { // Daftar Kekayaan Tanah Wakaf
                """
                <div class="header">
                    <h2>DAFTAR KEKAYAAN TANAH WAKAF NAZHIR</h2>
                </div>
                <p>Menerangkan daftar aset wakaf yang saat ini dikuasai, dijaga serta diverifikasi oleh Pengurus Nazhir Kecamatan ${land.landKecamatan} sesuai dengan ikrar wakif pada tanggal pencatatan:</p>
                
                <table class="data-table">
                    <thead>
                        <tr>
                            <th width="5%">No</th>
                            <th width="35%">Spesifikasi Aset Wakaf</th>
                            <th width="20%">Luas Tanah</th>
                            <th width="20%">Status Legalitas</th>
                            <th width="20%">Peruntukan</th>
                        </tr>
                    </thead>
                    <tbody>
                        <tr>
                            <td align="center">1</td>
                            <td>
                                <strong>Objek Tanah Wakif ${land.wakifName}</strong><br>
                                Desa ${land.landDesa}, Kecamatan ${land.landKecamatan}<br>
                                <span style="font-size: 11px; color: #555;">Koordinat: ${land.coordinateLat}, ${land.coordinateLng}</span>
                            </td>
                            <td align="center">${land.landArea} m²</td>
                            <td align="center">$certStatus<br>No. ${land.certificateOrLetterCNo}</td>
                            <td align="center">${land.purpose}</td>
                        </tr>
                    </tbody>
                </table>

                <p style="margin-top: 20px;">Catatan batas fisik tanah wakaf tersebut di lapangan:</p>
                <table class="info-table" style="margin-left: 20px; font-size: 13px;">
                    <tr><td width="15%">Utara</td><td>: ${land.boundaryNorth}</td><td width="15%">Selatan</td><td>: ${land.boundarySouth}</td></tr>
                    <tr><td>Timur</td><td>: ${land.boundaryEast}</td><td>Barat</td><td>: ${land.boundaryWest}</td></tr>
                </table>

                <p style="margin-top: 20px;">Pencatatan daftar kekayaan ini digunakan sebagai arsip inventarisasi Kementerian Agama RI serta Badan Wakaf Indonesia dalam mencegah pengalihan fungsi aset di kemudian hari.</p>

                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>

                <h4 style="text-align: center; margin-top: 20px; margin-bottom: 5px;">Hormat Kami, Pengurus Nazhir Wakaf:</h4>
                <table class="sign-table-three">
                    <tr>
                        <td width="33%">Ketua Nazhir,</td>
                        <td width="33%">Sekretaris Nazhir,</td>
                        <td width="33%">Bendahara Nazhir,</td>
                    </tr>
                    <tr class="spacer-small"><td></td><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${n1.name}</strong></td>
                        <td class="sign-name"><strong>${n2.name}</strong></td>
                        <td class="sign-name"><strong>${n3.name}</strong></td>
                    </tr>
                </table>
                """.trimIndent()
            }
            5 -> { // Surat Pernyataan Bersedia Diaudit
                """
                <div class="header">
                    <h2>SURAT PERNYATAAN BERSEDIA DIAUDIT</h2>
                </div>
                <p>Kami yang bertandatangan di bawah ini, selaku jajaran Pengurus Nazhir yang sah atas tanah wakaf yang berlokasi di Kel/Desa ${land.landDesa}, Kecamatan ${land.landKecamatan}, menyatakan dengan sadar dan penuh tanggung jawab bahwa:</p>
                
                <div class="declaration-box" style="border-left: 4px solid #034C3C; padding-left: 15px; margin: 20px 0;">
                    <p style="font-style: italic; font-size: 15px;">"Demi menjaga integritas, profesionalitas, akuntabilitas, serta menjamin pengelolaan dana dan pemanfaatan tanah wakaf secara syar'i dan bebas dari penyelewengan, kami dengan sukarela dan tanpa syarat menyatakan <strong>BERSEDIA UNTUK DIAUDIT SEWAKTU-WAKTU</strong> baik audit program maupun audit keuangan oleh lembaga audit independen atau instansi berwenang (Kementerian Agama RI/Badan Wakaf Indonesia)."</p>
                </div>

                <p>Pernyataan ini kami tanda tangani demi meningkatkan kepercayaan mustahik, wakif, dan masyarakat luas atas tata kelola aset wakaf agar tercapai kemaslahatan maksimal.</p>

                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>

                <h4 style="text-align: center; margin-top: 25px; margin-bottom: 5px;">Jajaran Nazhir yang Menyatakan:</h4>
                <table class="sign-table-three">
                    <tr>
                        <td width="33%">Ketua Nazhir,</td>
                        <td width="33%">Sekretaris Nazhir,</td>
                        <td width="33%">Bendahara Nazhir,</td>
                    </tr>
                    <tr class="spacer-small"><td></td><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${n1.name}</strong></td>
                        <td class="sign-name"><strong>${n2.name}</strong></td>
                        <td class="sign-name"><strong>${n3.name}</strong></td>
                    </tr>
                </table>
                """.trimIndent()
            }
            6 -> { // Surat Kuasa Balik Nama BPN
                """
                <div class="header">
                    <h2>SURAT KUASA BALIK NAMA SERTIPIKAT TANAH WAKAF</h2>
                </div>
                <p>Yang bertanda tangan di bawah ini selaku <strong>PEMBERI KUASA</strong> (susunan Pengurus Nazhir Penerima Wakaf):</p>
                
                <table class="info-table" style="margin-left: 15px; font-size: 13px;">
                    <tr><td width="20%"><strong>1. KETUA</strong></td><td>: ${n1.name} (NIK: ${n1.identityNo})</td></tr>
                    <tr><td><strong>2. SEKRETARIS</strong></td><td>: ${n2.name} (NIK: ${n2.identityNo})</td></tr>
                    <tr><td><strong>3. BENDAHARA</strong></td><td>: ${n3.name} (NIK: ${n3.identityNo})</td></tr>
                </table>

                <p style="margin-top: 15px;">Dengan ini memberikan kuasa penuh kepada (selaku <strong>PENERIMA KUASA</strong>):</p>
                <table class="info-table">
                    <tr><td width="30%"><strong>Nama Lengkap</strong></td><td>: ${land.authorizedRepresentativeName}</td></tr>
                    <tr><td><strong>NIK KTP</strong></td><td>: ${land.authorizedRepresentativeIdentityNo}</td></tr>
                    <tr><td><strong>Alamat Rumah</strong></td><td>: ${land.authorizedRepresentativeAddress}</td></tr>
                </table>

                <p>------------------------------------------ <strong>KHUSUS</strong> ------------------------------------------</p>
                <p>Untuk bertindak atas nama Pemberi Kuasa dalam mengurus proses <strong>Balik Nama Sertipikat Hak Milik</strong> menjadi <strong>Sertipikat Hak Milik Wakaf</strong> pada Kantor Pertanahan Nasional (BPN) setempat atas tanah aset wakaf:</p>
                
                <table class="info-table" style="margin-left: 15px;">
                    <tr><td width="30%">Asal Hak Milik (Wakif)</td><td>: ${land.wakifName}</td></tr>
                    <tr><td>Luas Tanah</td><td>: ${land.landArea} m²</td></tr>
                    <tr><td>Nomor Sertipikat/Letter C</td><td>: ${land.certificateOrLetterCNo}</td></tr>
                    <tr><td>Tempat / Lokasi Tanah</td><td>: Desa ${land.landDesa}, Kecamatan ${land.landKecamatan}</td></tr>
                </table>

                <p>Penerima kuasa berwenang untuk menghadap pejabat terkait, menandatangani dokumen pendaftaran, menyerahkan berkas dan mengambil sertipikat wakaf yang telah selesai diproses.</p>
                <p>Demikian surat kuasa ini dibuat untuk dipergunakan sebagaimana mestinya.</p>

                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>

                <table class="sign-table">
                    <tr>
                        <td width="55%">Para Pemberi Kuasa (NAZHIR),</td>
                        <td width="45%">Penerima Kuasa,</td>
                    </tr>
                    <tr class="spacer-small"><td></td><td></td></tr>
                    <tr>
                        <td style="font-size: 12px; line-height: 1.8;">
                            1. Ketua: <strong>${n1.name}</strong> (...............)<br>
                            2. Sekretaris: <strong>${n2.name}</strong> (...............)<br>
                            3. Bendahara: <strong>${n3.name}</strong> (...............)
                        </td>
                        <td style="vertical-align: bottom; font-weight: bold;" class="sign-name">
                            ${land.authorizedRepresentativeName}
                        </td>
                    </tr>
                </table>
                """.trimIndent()
            }
            7 -> { // Surat Pernyataan Bebas Sengketa
                """
                <div class="header">
                    <h2>SURAT PERNYATAAN BEBAS SENGKETA TANAH WAKAF</h2>
                </div>
                <p>Yang bertandatangan di bawah ini selaku jajaran Pengurus Nazhir Kecamatan ${land.landKecamatan} menyatakan dengan penuh kejujuran dan rasa tanggung jawab hukum bahwa sebidang tanah wakaf seluas:</p>
                
                <table class="info-table" style="margin: 15px 0 15px 15px;">
                    <tr><td width="35%"><strong>Luas Tanah Fisik</strong></td><td>: ${land.landArea} m²</td></tr>
                    <tr><td><strong>Bukti Asal Hak</strong></td><td>: $certStatus No. ${land.certificateOrLetterCNo}</td></tr>
                    <tr><td><strong>Nama Wakif Asal</strong></td><td>: ${land.wakifName}</td></tr>
                    <tr><td><strong>Lokasi Fisik</strong></td><td>: Kel/Desa ${land.landDesa}, Kecamatan ${land.landKecamatan}</td></tr>
                </table>

                <p>Menyatakan bahwa tanah tersebut:</p>
                <ol style="line-height: 1.8; margin-left: 20px;">
                    <li>Benar-benar aman secara penguasaan fisik di lapangan.</li>
                    <li><strong>BEBAS DARI SEGALA JENIS SENGKETA</strong> kepemilikan maupun sengketa batas dengan tetangga sekitar.</li>
                    <li>Bebas dari sitaan jaminan hutang bank formal maupun non-formal.</li>
                    <li>Tidak sedang berada dalam gugatan waris atau perkara perdata di Pengadilan Negeri maupun Pengadilan Agama.</li>
                </ol>

                <p>Apabila di kemudian hari pernyataan ini terbukti tidak benar dan mengakibatkan tuntutan hukum, maka kami selaku pengurus Nazhir bersedia menanggung segala akibat hukum secara moral maupun materiil sesuai ketentuan perundangan yang berlaku.</p>
                <p>Demikian surat pernyataan sengketa ini dibuat secara sadar untuk dijadikan bukti administrasi di KUA dan BPN.</p>

                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>

                <h4 style="text-align: center; margin-top: 20px; margin-bottom: 5px;">Pengurus Nazhir yang Menyatakan:</h4>
                <table class="sign-table-three">
                    <tr>
                        <td width="33%">Ketua Nazhir,</td>
                        <td width="33%">Sekretaris Nazhir,</td>
                        <td width="33%">Bendahara Nazhir,</td>
                    </tr>
                    <tr class="spacer-small"><td></td><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${n1.name}</strong></td>
                        <td class="sign-name"><strong>${n2.name}</strong></td>
                        <td class="sign-name"><strong>${n3.name}</strong></td>
                    </tr>
                </table>
                """.trimIndent()
            }
            8 -> { // Surat Persetujuan Suami/Istri Wakif
                """
                <div class="header">
                    <h2>SURAT PERSETUJUAN PERNYATAAN WAKAF (PASANGAN)</h2>
                </div>
                <p>Yang bertanda tangan di bawah ini:</p>
                <table class="info-table">
                    <tr><td width="30%"><strong>Nama Lengkap (Spouse)</strong></td><td>: ${land.spouseName}</td></tr>
                    <tr><td><strong>NIK (KTP)</strong></td><td>: ${land.spouseIdentityNo}</td></tr>
                    <tr><td><strong>Umur</strong></td><td>: ${land.spouseAge} Tahun</td></tr>
                    <tr><td><strong>Pekerjaan</strong></td><td>: ${land.spouseProfession}</td></tr>
                    <tr><td><strong>Alamat Rumah</strong></td><td>: ${land.spouseAddress}</td></tr>
                </table>

                <p>Menerangkan bahwa saya adalah <strong>ISTRI/SUAMI SAH</strong> dari Wakif:</p>
                <table class="info-table">
                    <tr><td width="30%"><strong>Nama Lengkap (Wakif)</strong></td><td>: ${land.wakifName}</td></tr>
                    <tr><td><strong>Alamat Rumah</strong></td><td>: ${land.wakifAddress}</td></tr>
                </table>

                <p>Dengan ini menyatakan memberikan <strong>PERSETUJUAN SEPENUHNYA</strong> kepada suami/istri saya untuk mewakafkan sebidang tanah milik keluarga atau harta bersama:</p>
                <table class="info-table" style="margin-left: 15px;">
                    <tr><td width="30%">Luas Tanah Wakaf</td><td>: ${land.landArea} m²</td></tr>
                    <tr><td>Nomor Sertipikat/Letter C</td><td>: ${land.certificateOrLetterCNo}</td></tr>
                    <tr><td>Alamat Obyek Wakaf</td><td>: Kel/Desa ${land.landDesa}, Kecamatan ${land.landKecamatan}</td></tr>
                    <tr><td>Peruntukan Wakaf</td><td>: ${land.purpose}</td></tr>
                </table>

                <p>Kami menyadari dan ikhlas melepas hak keperdataan kami atas tanah tersebut semata-mata mengharapkan keridhoan Allah SWT dan menjadikannya amal jariyah bagi keluarga kami demi pembangunan umat.</p>
                <p>Demikian surat persetujuan pasangan ini ditandatangani bersama tanpa ada paksaan.</p>

                <div class="date-location">
                    ${land.landDesa}, $creationDate
                </div>

                <table class="sign-table">
                    <tr>
                        <td width="50%">Yang memberikan persetujuan,<br>(SUAMI/ISTRI WAKIF)</td>
                        <td width="50%">Pihak yang mewakafkan,<br>(WAKIF UTAMA)</td>
                    </tr>
                    <tr class="spacer"><td></td><td></td></tr>
                    <tr>
                        <td class="sign-name"><strong>${land.spouseName}</strong></td>
                        <td class="sign-name"><strong>${land.wakifName}</strong></td>
                    </tr>
                </table>
                """.trimIndent()
            }
            9 -> { // Piagam Penghargaan Wakif (Award Certificate)
                """
                <div class="certificate-container" style="border: 6px double #C89832; padding: 25px; border-radius: 12px; background-color: #FAF8F5; text-align: center; position: relative;">
                    <div style="font-size: 20px; font-family: serif; font-weight: bold; color: #034C3C; text-transform: uppercase; margin-bottom: 5px; tracking: 4px;">
                        Piagam Penghargaan Wakif
                    </div>
                    <div style="font-size: 12px; color: #777; margin-bottom: 25px; font-style: italic; font-weight: 500;">
                        No. Registrasi: L-WKF/AL-HIKMAH/${land.id}
                    </div>

                    <div style="font-style: italic; font-size: 16px; margin-bottom: 10px; color: #444;">
                        Diberikan dengan penuh rasa hormat dan syukur kepada:
                    </div>
                    <div style="font-size: 26px; font-family: Georgia, serif; font-weight: bold; color: #034C3C; margin-bottom: 15px; border-bottom: 2px solid #C89832; display: inline-block; padding: 0 30px 5px 30px;">
                        ${land.wakifName}
                    </div>

                    <p style="font-size: 15px; line-height: 1.8; margin: 15px auto; max-width: 90%; color: #222;">
                        Atas kedermawanan, ketulusan serta keikhlasannya dalam mewakafkan tanah seluar <strong>${land.landArea} m²</strong> ($certStatus No: <strong>${land.certificateOrLetterCNo}</strong>) yang terletak di <strong>Desa ${land.landDesa}, Kecamatan ${land.landKecamatan}</strong> untuk pembangunan <strong>${land.purpose}</strong>.
                    </p>

                    <p style="font-size: 13px; font-style: italic; color: #0C9F7F; font-weight: bold; border-top: 1px dashed #C89832; border-bottom: 1px dashed #C89832; display: inline-block; padding: 5px 25px; margin: 10px 0;">
                        "Jika seseorang meninggal dunia, maka terputuslah amalannya kecuali tiga perkara: sedekah jariyah (wakaf), ilmu bermanfaat, dan doa anak saleh." — (HR. Muslim)
                    </p>

                    <p style="font-size: 14px; margin-top: 15px; color: #333;">
                        Semoga amal jariyah ini senantiasa mengalirkan pahala yang tiada terputus, memberikan barokah bagi keluarga, dan diridhoi oleh Allah subhanahu wa ta'ala. Aamiin yaa Robbal 'Aalamiin.
                    </p>

                    <div class="date-location" style="text-align: right; margin-top: 30px; font-size: 13px; padding-right: 40px;">
                        Ditetapkan di ${land.landDesa}, $creationDate
                    </div>

                    <table class="sign-table" style="margin-top: 10px; width: 100%;">
                        <tr>
                            <td width="50%" align="center" style="font-size: 12px; font-weight: bold; color: #555;">
                                Kepala KUA Kecamatan ${land.landKecamatan},
                                <br><br><br><br><br>
                                <strong>${land.kuaHeadName}</strong>
                            </td>
                            <td width="50%" align="center" style="font-size: 12px; font-weight: bold; color: #555;">
                                Jajaran Pengurus Nazhir,
                                <br><br><br><br><br>
                                <strong>${n1.name}</strong>
                            </td>
                        </tr>
                    </table>
                </div>
                """.trimIndent()
            }
            else -> "<p>Dokumen tidak dikenal.</p>"
        }

        return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="utf-8">
            <style>
                body {
                    font-family: 'Helvetica Neue', Helvetica, Arial, sans-serif;
                    line-height: 1.6;
                    color: #1a231f;
                    background-color: #ffffff;
                    margin: 0;
                    padding: 20px;
                    font-size: 13.5px;
                }
                .header {
                    text-align: center;
                    border-bottom: 3px double #034C3C;
                    padding-bottom: 15px;
                    margin-bottom: 20px;
                }
                .header h2 {
                    margin: 0;
                    color: #034C3C;
                    font-size: 18px;
                    letter-spacing: 1px;
                }
                .header h3 {
                    margin: 5px 0 0 0;
                    font-size: 14px;
                    color: #0C9F7F;
                }
                .info-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-bottom: 15px;
                }
                .info-table td {
                    padding: 4px 6px;
                    vertical-align: top;
                }
                .data-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 15px;
                    margin-bottom: 15px;
                }
                .data-table th, .data-table td {
                    border: 1px solid #ddd;
                    padding: 8px;
                }
                .data-table th {
                    background-color: #f2f8f6;
                    color: #034C3C;
                    font-weight: bold;
                }
                .date-location {
                    text-align: right;
                    margin-top: 35px;
                    margin-bottom: 20px;
                    font-size: 13px;
                }
                .sign-table {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 15px;
                }
                .sign-table td {
                    text-align: center;
                    padding: 8px;
                    vertical-align: top;
                }
                .sign-table-three {
                    width: 100%;
                    border-collapse: collapse;
                    margin-top: 15px;
                }
                .sign-table-three td {
                    text-align: center;
                    padding: 8px;
                    vertical-align: top;
                }
                .spacer {
                    height: 80px;
                }
                .spacer-small {
                    height: 60px;
                }
                .sign-name {
                    text-decoration: underline;
                }
                .program-block {
                    background-color: #fafcfb;
                    border-left: 3px solid #0C9F7F;
                    padding: 10px 15px;
                    margin-bottom: 15px;
                    border-radius: 0 4px 4px 0;
                    font-style: italic;
                }
                h3 {
                    font-size: 14px;
                    color: #034C3C;
                    margin-top: 20px;
                    margin-bottom: 8px;
                }
                hr {
                    border: 0;
                    border-top: 1px solid #eee;
                    margin: 15px 0;
                }
            </style>
        </head>
        <body>
            $bodyContent
        </body>
        </html>
        """.trimIndent()
    }

    // Generate formatted raw string suitable for standard clipboard copy or exporting to .doc simple string
    fun generateRawText(docType: Int, land: WakafLand, nazhirs: List<Nazhir>): String {
        val n1 = nazhirs.getOrNull(0) ?: Nazhir(name = "(Nama Ketua Nazhir)", identityNo = "-", age = 0, profession = "-", address = "-", kecamatan = "-", position = "Ketua")
        val n2 = nazhirs.getOrNull(1) ?: Nazhir(name = "(Nama Sekretaris Nazhir)", identityNo = "-", age = 0, profession = "-", address = "-", kecamatan = "-", position = "Sekretaris")
        val n3 = nazhirs.getOrNull(2) ?: Nazhir(name = "(Nama Bendahara Nazhir)", identityNo = "-", age = 0, profession = "-", address = "-", kecamatan = "-", position = "Bendahara")

        val creationDate = dateFormatter.format(Date(land.createdAt))
        val certStatus = if (land.isCertified) "Sertipikat Hak Milik" else "Letter C / Belum Bersertipikat"

        val stringBuilder = StringBuilder()
        stringBuilder.append("====================================================\n")
        stringBuilder.append("         ${getDocumentTitle(docType).uppercase(Locale.getDefault())}\n")
        stringBuilder.append("====================================================\n\n")

        when (docType) {
            1 -> {
                stringBuilder.append("Yang bertandatangan di bawah ini (Pihak Wakif):\n")
                stringBuilder.append("Nama Lengkap : ${land.wakifName}\n")
                stringBuilder.append("NIK / KTP    : ${land.wakifIdentityNo}\n")
                stringBuilder.append("Umur         : ${land.wakifAge} Tahun\n")
                stringBuilder.append("Pekerjaan    : ${land.wakifProfession}\n")
                stringBuilder.append("Alamat       : ${land.wakifAddress}\n\n")
                stringBuilder.append("Menyatakan dengan sesungguhnya mewakafkan sebidang tanah milik saya:\n")
                stringBuilder.append("Luas Tanah   : ${land.landArea} m²\n")
                stringBuilder.append("Status       : $certStatus\n")
                stringBuilder.append("Nomor Surat  : ${land.certificateOrLetterCNo}\n")
                stringBuilder.append("Lokasi       : Kel/Desa ${land.landDesa}, Kecamatan ${land.landKecamatan}\n")
                stringBuilder.append("Koordinat    : ${land.coordinateLat}, ${land.coordinateLng}\n")
                stringBuilder.append("Peruntukan   : ${land.purpose}\n\n")
                stringBuilder.append("Batas - Batas Obyek:\n")
                stringBuilder.append("- Utara      : ${land.boundaryNorth}\n")
                stringBuilder.append("- Selatan    : ${land.boundarySouth}\n")
                stringBuilder.append("- Timur      : ${land.boundaryEast}\n")
                stringBuilder.append("- Barat      : ${land.boundaryWest}\n\n")
                stringBuilder.append("Menyatakan pula bahwa tanah wakaf tersebut di atas bebas dari hukum, TIDAK DALAM SENGKETA dengan pihak manapun, serta TIDAK SEDANG DIJAMINKAN KE BANK sebagai agunan pinjaman.\n\n")
                stringBuilder.append("Saksi-Saksi:\n")
                stringBuilder.append("Saksi I: ${land.witness1Name}\n")
                stringBuilder.append("Saksi II: ${land.witness2Name}\n\n")
                stringBuilder.append("Mengetahui:\n")
                stringBuilder.append("Kepala Desa/Lurah Kel. ${land.landDesa}: ${land.villageHeadName}\n")
                stringBuilder.append("Camat Kecamatan ${land.landKecamatan}: ${land.camatName}\n\n")
            }
            2 -> {
                stringBuilder.append("Perihal: Permohonan Pendaftaran Nazhir\n\n")
                stringBuilder.append("Kepada Yth,\nKepala Kantor Urusan Agama (KUA) Kecamatan ${land.landKecamatan}\nDi Tempat\n\n")
                stringBuilder.append("Dengan ini kami mengajukan permohonan pendaftaran 3 orang Nazhir atas tanah wakaf yang diserahkan oleh:\n")
                stringBuilder.append("- Wakif: ${land.wakifName}\n")
                stringBuilder.append("- Bukti: $certStatus No ${land.certificateOrLetterCNo}\n")
                stringBuilder.append("- Wilayah: ${land.landDesa}, ${land.landKecamatan}\n\n")
                stringBuilder.append("Susunan Pengurus Nazhir Lengkap:\n")
                stringBuilder.append("1. Ketua:\n   Nama: ${n1.name}\n   NIK: ${n1.identityNo}\n   Pekerjaan: ${n1.profession}\n   Alamat: ${n1.address}\n")
                stringBuilder.append("2. Sekretaris:\n   Nama: ${n2.name}\n   NIK: ${n2.identityNo}\n   Pekerjaan: ${n2.profession}\n   Alamat: ${n2.address}\n")
                stringBuilder.append("3. Bendahara:\n   Nama: ${n3.name}\n   NIK: ${n3.identityNo}\n   Pekerjaan: ${n3.profession}\n   Alamat: ${n3.address}\n\n")
                stringBuilder.append("Hormat Kami,\n(1) ${n1.name}\n(2) ${n2.name}\n(3) ${n3.name}\n")
            }
            3 -> {
                stringBuilder.append("Obyek: Tanah Wakaf ${land.wakifName} (${land.landArea} m²)\n")
                stringBuilder.append("Peruntukan: ${land.purpose}\n\n")
                stringBuilder.append("RENCANA PROGRAM KERJA NAZHIR:\n\n")
                stringBuilder.append("A. PROGRAM JANGKA PENDEK (0-1 Tahun):\n")
                stringBuilder.append("   ${land.programShortTerm}\n\n")
                stringBuilder.append("B. PROGRAM JANGKA MENENGAH (1-5 Tahun):\n")
                stringBuilder.append("   ${land.programMediumTerm}\n\n")
                stringBuilder.append("C. PROGRAM JANGKA PANJANG (Di atas 5 Tahun):\n")
                stringBuilder.append("   ${land.programLongTerm}\n\n")
                stringBuilder.append("Hormat kami,\nWakif: ${land.wakifName}\t\tKetua Nazhir: ${n1.name}\n")
            }
            4 -> {
                stringBuilder.append("Daftar Inventarisasi Kekayaan Tanah Wakaf yang dikuasai Nazhir Kecamatan ${land.landKecamatan}:\n\n")
                stringBuilder.append("Aset No 1:\n")
                stringBuilder.append("- Nama Wakif Asal: ${land.wakifName}\n")
                stringBuilder.append("- Luas Tanah      : ${land.landArea} m²\n")
                stringBuilder.append("- Status/No Surat : $certStatus / No. ${land.certificateOrLetterCNo}\n")
                stringBuilder.append("- Lokasi Fisik    : Kel. ${land.landDesa}, Kec. ${land.landKecamatan}\n")
                stringBuilder.append("- Koordinat GPS   : ${land.coordinateLat}, ${land.coordinateLng}\n")
                stringBuilder.append("- Peruntukan      : ${land.purpose}\n")
                stringBuilder.append("- Batas Lapangan  : Utara: ${land.boundaryNorth}, Selatan: ${land.boundarySouth}, Timur: ${land.boundaryEast}, Barat: ${land.boundaryWest}\n\n")
                stringBuilder.append("Disahkan oleh Pengurus Nazhir:\n")
                stringBuilder.append("Ketua: ${n1.name}\nSekretaris: ${n2.name}\nBendahara: ${n3.name}\n")
            }
            5 -> {
                stringBuilder.append("Menyatakan dengan sesungguhnya jajaran Pengurus Nazhir berjanji:\n\n")
                stringBuilder.append("\"Untuk menjaga kredibilitas wakaf dan ketaatan syar'i, kami menyatakan BERSEDIA DIAUDIT SEWAKTU-WAKTU oleh lembaga audit resmi independen atau Kantor Kementerian Agama RI.\"\n\n")
                stringBuilder.append("Yang menyatakan:\n")
                stringBuilder.append("Ketua Nazhir      : ${n1.name}\n")
                stringBuilder.append("Sekretaris Nazhir : ${n2.name}\n")
                stringBuilder.append("Bendahara Nazhir  : ${n3.name}\n")
            }
            6 -> {
                stringBuilder.append("PEMBERI KUASA (Nazhir):\n")
                stringBuilder.append("1. ${n1.name} (Ketua)\n")
                stringBuilder.append("2. ${n2.name} (Sekretaris)\n")
                stringBuilder.append("3. ${n3.name} (Bendahara)\n\n")
                stringBuilder.append("PENERIMA KUASA:\n")
                stringBuilder.append("Nama      : ${land.authorizedRepresentativeName}\n")
                stringBuilder.append("NIK / KTP : ${land.authorizedRepresentativeIdentityNo}\n")
                stringBuilder.append("Alamat    : ${land.authorizedRepresentativeAddress}\n\n")
                stringBuilder.append("Melakukan pendataraan Balik Nama Sertipikat Hak Milik atas nama Wakif ${land.wakifName} menjadi milik sertifikat wakaf pada instansi Kantor Pertanahan Nasional / BPN setempat.\n")
            }
            7 -> {
                stringBuilder.append("Kami selaku Pengurus Nazhir menerangkan dengan seksama bahwa:\n\n")
                stringBuilder.append("Tanah Wakaf Asal Wakif: ${land.wakifName}\n")
                stringBuilder.append("Luas: ${land.landArea} m²\n")
                stringBuilder.append("No Dokumen: ${land.certificateOrLetterCNo}\n")
                stringBuilder.append("Kel/Desa: ${land.landDesa}, Kec: ${land.landKecamatan}\n\n")
                stringBuilder.append("Tanah tersebut benar-benar aman, bebas dari sitaan jaminan hutang bank dan BEBAS DARI SEGALA MACAM BENTUK SENGKETA hukum dengan pihak mana pun.\n")
            }
            8 -> {
                stringBuilder.append("Yang bertanda tangan di bawah ini (Istri/Suami sah Wakif):\n")
                stringBuilder.append("Nama Lengkap : ${land.spouseName}\n")
                stringBuilder.append("NIK / KTP    : ${land.spouseIdentityNo}\n")
                stringBuilder.append("Alamat       : ${land.spouseAddress}\n\n")
                stringBuilder.append("Menyetujui keputusan suami/istri saya (${land.wakifName}) untuk mewakafkan tanah seluas ${land.landArea} m² dengan nomor surat ${land.certificateOrLetterCNo} demi kebaikan umat.\n")
            }
            9 -> {
                stringBuilder.append("P I A G A M   P E N G H A R G A A N   W A K I F\n")
                stringBuilder.append("----------------------------------------------------\n\n")
                stringBuilder.append("Diberikan kepada:\n\n")
                stringBuilder.append("          ${land.wakifName.uppercase(Locale.getDefault())}\n\n")
                stringBuilder.append("Atas keikhlasannya mewakafkan tanah seluas ${land.landArea} m² untuk pembangunan ${land.purpose}.\n")
                stringBuilder.append("Semoga Allah senantiasa melimpahkan pahala jariyah serta keberkahan berlipat ganda.\n")
            }
        }

        stringBuilder.append("\n\nDibuat di: Kel. ${land.landDesa}, Tanggal: $creationDate\n")
        stringBuilder.append("====================================================\n")
        return stringBuilder.toString()
    }
}
