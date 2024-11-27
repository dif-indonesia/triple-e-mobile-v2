package id.co.dif.base_project.data

/***
 * Created by kikiprayudi
 * on Thursday, 22/06/23 18:24
 *
 */


data class DirectionsResponse(
    var routes: ArrayList<Route>,
)

data class Route(
    var legs: ArrayList<Leg>,
)

data class Leg(
    var distance: Distance,
    var steps: ArrayList<Step>,
    var duration: Duration
)
data class Distance(
    var text : String,
    var value: Int
)

data class Step(
    var polyline: Polyline,
)

data class Polyline(
    var points: String,
)

data class Duration(
    var text: String,
    var value: Int
)
