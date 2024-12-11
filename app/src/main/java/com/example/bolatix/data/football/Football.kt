package com.example.bolatix.data.football

import com.example.bolatix.data.models.GateModel
import com.example.bolatix.data.models.StadionModel
import com.example.bolatix.data.models.TeamModel


object Football {
    private const val BASE_URL = "https://cdns.klimg.com/bola.net/library/upload/"
    val teams: List<TeamModel> = listOf(
        TeamModel(1, "Persebaya", "${BASE_URL}23/2020/02/persebaya_b184ad7.png"),
        TeamModel(2, "PERSIB", "${BASE_URL}23/2020/02/persib_b175e6f.png"),
        TeamModel(3, "Borneo FC", "${BASE_URL}23/2020/02/borneo_b182b52.png"),
        TeamModel(4, "Bali United", "${BASE_URL}23/2020/02/bali-utd_bcbda2b.png"),
        TeamModel(5, "Persija", "${BASE_URL}23/2022/07/persija-logo_eabb41c.png"),
        TeamModel(6, "PSM Makassar", "${BASE_URL}23/2020/02/psm-makasar_bbe9415.png"),
        TeamModel(7, "PSBS Biak", "${BASE_URL}23/2024/08/psbs-biak_a0ce1cf.png"),
        TeamModel(8, "Arema FC", "${BASE_URL}23/2020/02/arema_bdc6643.png"),
        TeamModel(9, "Persita", "${BASE_URL}23/2020/02/persita_bb0e20d.png"),
        TeamModel(10, "Persik", "${BASE_URL}23/2020/02/persik_b857ec3.png"),
        TeamModel(11, "Dewa United", "${BASE_URL}23/2022/07/dewa-united-logo_906c24a.png"),
        TeamModel(12, "Malut United", "${BASE_URL}23/2024/08/malut_5960d0b.png"),
        TeamModel(13, "PSIS", "${BASE_URL}23/2020/02/psis_babea4d.png"),
        TeamModel(14, "Barito Putera", "${BASE_URL}23/2020/02/barito_ba877b1.png"),
        TeamModel(15, "PSS Sleman", "${BASE_URL}23/2020/02/pss-sleman_babaf81.png"),
        TeamModel(16, "Persis", "${BASE_URL}23/2022/07/persis-solo-png_a8f30fa.png"),
        TeamModel(17, "Madura United", "${BASE_URL}23/2020/02/madura-united_b184730.png"),
        TeamModel(18, "Semen Padang", "${BASE_URL}23/2024/08/semen-padang_1b74bd2.png"),
    )
    val tickets: List<GateModel> = listOf(
        GateModel(1,"EKONOMI BARAT (GATE A)", 150000),
        GateModel(2,"VIP BARAT (GATE A)", 250000),
        GateModel(3,"EKONOMI TIMUR (GATE D-F-G)", 140000),
        GateModel(4,"VIP TIMUR (GATE B)", 300000),
        GateModel(5,"REGULAR (GATE H-I)", 100000),
        GateModel(6,"REGULER (GATE J-K)", 100000),
        GateModel(7, "SUPER VIP (GATE A)", 500000),
        GateModel(8, "EKONOMI UTARA (GATE L-M)", 120000)
    )
    val stadion: List<StadionModel> = listOf(
        StadionModel("Stadion Si Jalak Harupat"),
        StadionModel("Stadion Gelora Bangkalan"),
        StadionModel("Stadion Batakan"),
        StadionModel("Jakarta International Stadium"),
        StadionModel("Stadion Madya Magelang"),
        StadionModel("Stadion Brawijaya"),
        StadionModel("Stadion Gelora Bung Tomo"),
        StadionModel("Stadion Gelora Soeprijadi"),
        StadionModel("Stadion PTIK"),
        StadionModel("Stadion Kapten I Wayan Dipta"),
        StadionModel("Stadion Utama Gelora Bung Karno"),
        StadionModel("Stadion Sultan Agung"),
        StadionModel("Stadion Manahan"),
        StadionModel("Stadion Gelora Bangkalan"),
        StadionModel("Stadion Pakansari"),
        StadionModel("Stadion Kapten I Wayan Dipta"),
        StadionModel("Stadion Sultan Agung"),
        StadionModel("Stadion Manahan"),
        StadionModel("Stadion Utama Sumatera Barat"),
        StadionModel("Stadion Moch. Soebroto"),
        StadionModel("Stadion Gelora Soeprojadi"),
        StadionModel("Stadion Geloran Bangkalan"),
        StadionModel("Stadion Haji Agus Salim"),
        StadionModel("Stadion Demang Lehman"),
        StadionModel("Stadion Lukas Enembe"),
        StadionModel("Stadion Pekansari"),
        StadionModel("Stadion Gelora Soepriadi"),
        StadionModel("Stadion Dipta")
    )
}