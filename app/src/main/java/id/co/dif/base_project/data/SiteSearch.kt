package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName


data class SiteSearchData (
    val limit : Int,
    val list : SiteSearch

)


data class SiteSearch (
    @SerializedName("TT Site All")
    val TT_Site_All : List<SiteDetails>
)


data class InfoSite (
    val info_site : SiteData

)

data class SiteData (
    @SerializedName("info_site")
    val info_site : SiteDetails,
    @SerializedName("site_history")
    val site_history : List<SiteHistory>,
) : BaseData()

data class GetSiteHistory (
    @SerializedName("site_history")
    val site_history : SiteDetails
)



data class SearchengineerData (
    val limit : Int,
    val list : SelectEngineer

)

data class SelectEngineer (
    @SerializedName("TT Map Me")
    val TT_Site_All : List<MapMe>
)

data class Mapmesearch(
    val limit: Int,
    val list_ticket: MapMee
)

data class MapMee (
    val list_ticket : Location
)