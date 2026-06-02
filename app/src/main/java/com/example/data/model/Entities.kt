package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "nazhir_table")
data class Nazhir(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val identityNo: String,
    val age: Int,
    val profession: String,
    val address: String,
    val kecamatan: String,
    val position: String // e.g. Ketua, Sekretaris, Bendahara
)

@Entity(tableName = "wakaf_land_table")
data class WakafLand(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    
    // Wakif Details
    val wakifName: String,
    val wakifIdentityNo: String,
    val wakifAge: Int,
    val wakifProfession: String,
    val wakifAddress: String,
    
    // Spouse Details
    val spouseName: String,
    val spouseIdentityNo: String,
    val spouseAge: Int,
    val spouseProfession: String,
    val spouseAddress: String,
    
    // Land Details
    val landKecamatan: String,
    val landDesa: String,
    val landArea: Double, // Luas m2
    val isCertified: Boolean,
    val certificateOrLetterCNo: String,
    
    // Boundaries
    val boundaryNorth: String,
    val boundarySouth: String,
    val boundaryEast: String,
    val boundaryWest: String,
    
    // Coordinates
    val coordinateLat: Double = 0.0,
    val coordinateLng: Double = 0.0,
    
    // Purpose and Program Kerja Development
    val purpose: String, // Peruntukan (e.g., Masjid, Pesantren, Makam)
    val programShortTerm: String,
    val programMediumTerm: String,
    val programLongTerm: String,
    
    // Selected Nazhir complete Details (snapshot or links; for ease of printing, we can snapshot or save selected IDs)
    val nazhir1Id: Long,
    val nazhir2Id: Long,
    val nazhir3Id: Long,
    
    // Witnesses Complete Details (2 witnesses)
    val witness1Name: String,
    val witness1IdentityNo: String,
    val witness1Age: Int,
    val witness1Profession: String,
    val witness1Address: String,
    
    // Second Witness
    val witness2Name: String,
    val witness2IdentityNo: String,
    val witness2Age: Int,
    val witness2Profession: String,
    val witness2Address: String,
    
    // Authorities / Additional officials
    val villageHeadName: String, // Kepala Desa / Lurah
    val camatName: String, // Camat
    val kuaHeadName: String, // Kepala KUA
    val authorizedRepresentativeName: String, // Penerima kuasa balik nama BPN
    val authorizedRepresentativeIdentityNo: String,
    val authorizedRepresentativeAddress: String,
    
    val createdAt: Long = System.currentTimeMillis()
)
