package com.example.data.repository

import com.example.data.dao.WakafDao
import com.example.data.model.Nazhir
import com.example.data.model.WakafLand
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class WakafRepository(private val wakafDao: WakafDao) {

    val allWakafLands: Flow<List<WakafLand>> = wakafDao.getAllWakafLands()
    val allNazhirs: Flow<List<Nazhir>> = wakafDao.getAllNazhirs()

    fun getWakafLandById(id: Long): Flow<WakafLand?> {
        return wakafDao.getWakafLandById(id)
    }

    fun getNazhirsByKecamatan(kecamatan: String): Flow<List<Nazhir>> {
        return wakafDao.getNazhirsByKecamatan(kecamatan)
    }

    suspend fun getNazhirsByIds(ids: List<Long>): List<Nazhir> {
        return wakafDao.getNazhirsByIds(ids)
    }

    suspend fun insertWakafLand(wakafLand: WakafLand): Long {
        return wakafDao.insertWakafLand(wakafLand)
    }

    suspend fun deleteWakafLandById(id: Long) {
        wakafDao.deleteWakafLandById(id)
    }

    suspend fun insertNazhir(nazhir: Nazhir): Long {
        return wakafDao.insertNazhir(nazhir)
    }

    suspend fun deleteNazhirById(id: Long) {
        wakafDao.deleteNazhirById(id)
    }

    // Helper to populate default Nazhirs if the table is empty
    suspend fun initializeDefaultNazhirsIfNeeded() {
        val existing = wakafDao.getAllNazhirs().first()
        if (existing.isEmpty()) {
            val defaults = listOf(
                // Kendal
                Nazhir(
                    name = "KH. Muhammad Muzakir, M.A",
                    identityNo = "3324011010740001",
                    age = 52,
                    profession = "Dosen / Tokoh Agama",
                    address = "Jl. Karangsari No. 12, Kendal",
                    kecamatan = "Kendal",
                    position = "Ketua"
                ),
                Nazhir(
                    name = "H. Akhmad Suwandi, S.Pd",
                    identityNo = "3324012409790004",
                    age = 47,
                    profession = "PNS (Pegawai Negeri Sipil)",
                    address = "Jl. Pegulon No. 4, Kendal",
                    kecamatan = "Kendal",
                    position = "Sekretaris"
                ),
                Nazhir(
                    name = "Ust. Ahmad Solikhin, S.Pd.I",
                    identityNo = "3324010903850002",
                    age = 41,
                    profession = "Tenaga Pendidik",
                    address = "Jl. Patukangan No. 7, Kendal",
                    kecamatan = "Kendal",
                    position = "Bendahara"
                ),
                
                // Weleri
                Nazhir(
                    name = "KH. Ali Hasan, S.Ag",
                    identityNo = "3324021508810001",
                    age = 45,
                    profession = "Pegawai Swasta / Tokoh Agama",
                    address = "Jl. Penyangkringan No. 45, Weleri",
                    kecamatan = "Weleri",
                    position = "Ketua"
                ),
                Nazhir(
                    name = "H. Slamet Widodo",
                    identityNo = "3324021202760003",
                    age = 50,
                    profession = "Guru",
                    address = "Jl. Nawangsari No. 3, Weleri",
                    kecamatan = "Weleri",
                    position = "Sekretaris"
                ),
                Nazhir(
                    name = "Ust. Khoirul Anam, S.Pd.I",
                    identityNo = "3324021505880002",
                    age = 38,
                    profession = "Wiraswasta",
                    address = "Jl. Weleri Indah No. 82, Weleri",
                    kecamatan = "Weleri",
                    position = "Bendahara"
                ),
                
                // Kaliwungu
                Nazhir(
                    name = "Drs. KH. Asy'ari",
                    identityNo = "3324031506710001",
                    age = 55,
                    profession = "Tokoh Agama",
                    address = "Jl. Krajan Kulon No. 21, Kaliwungu",
                    kecamatan = "Kaliwungu",
                    position = "Ketua"
                ),
                Nazhir(
                    name = "H. Agus Salim",
                    identityNo = "3324032111830005",
                    age = 43,
                    profession = "Wiraswasta",
                    address = "Jl. Kutoharjo Indah No. 15, Kaliwungu",
                    kecamatan = "Kaliwungu",
                    position = "Sekretaris"
                ),
                Nazhir(
                    name = "M. Nur Sya'bani, S.Ak",
                    identityNo = "3324031402870003",
                    age = 39,
                    profession = "Akuntan",
                    address = "Jl. Mororejo No. 9, Kaliwungu",
                    kecamatan = "Kaliwungu",
                    position = "Bendahara"
                ),
                
                // Boja
                Nazhir(
                    name = "KH. Ahmad Rofiq, S.Th.I",
                    identityNo = "3324041112770001",
                    age = 49,
                    profession = "Pendakwah",
                    address = "Jl. Salamsari No. 32, Boja",
                    kecamatan = "Boja",
                    position = "Ketua"
                ),
                Nazhir(
                    name = "H. Bambang Purwanto, M.Si",
                    identityNo = "3324042805800004",
                    age = 46,
                    profession = "PNS (Pegawai Negeri Sipil)",
                    address = "Jl. Campurejo No. 14, Boja",
                    kecamatan = "Boja",
                    position = "Sekretaris"
                ),
                Nazhir(
                    name = "Ust. Mulyadi",
                    identityNo = "3324041910840002",
                    age = 42,
                    profession = "Pengusaha",
                    address = "Jl. Purwogondo No. 67, Boja",
                    kecamatan = "Boja",
                    position = "Bendahara"
                ),

                // Sukorejo
                Nazhir(
                    name = "KH. Sholihun",
                    identityNo = "3324052010720003",
                    age = 54,
                    profession = "Wiraswasta / Tokoh Agama",
                    address = "Jl. Kebumen No. 10, Sukorejo",
                    kecamatan = "Sukorejo",
                    position = "Ketua"
                ),
                Nazhir(
                    name = "H. Triyono",
                    identityNo = "3324051411800001",
                    age = 46,
                    profession = "Wiraswasta",
                    address = "Jl. Bringinsari No. 25, Sukorejo",
                    kecamatan = "Sukorejo",
                    position = "Sekretaris"
                ),
                Nazhir(
                    name = "Ust. Khoirudin",
                    identityNo = "3324050505850002",
                    age = 41,
                    profession = "Swasta",
                    address = "Jl. Genting Gunung No. 5, Sukorejo",
                    kecamatan = "Sukorejo",
                    position = "Bendahara"
                )
            )
            wakafDao.insertNazhirs(defaults)
        }
    }
}
