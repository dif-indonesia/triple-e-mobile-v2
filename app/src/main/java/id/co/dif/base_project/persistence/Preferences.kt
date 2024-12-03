package id.co.dif.base_project.persistence

import id.co.dif.base_project.data.BasicInfo
import id.co.dif.base_project.data.Education
import id.co.dif.base_project.data.Employee
import id.co.dif.base_project.data.LastLocation
import id.co.dif.base_project.data.TicketDetails
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.LocationScheduleCommand
import id.co.dif.base_project.data.LoginData
import id.co.dif.base_project.data.Note
import id.co.dif.base_project.data.Rca
import id.co.dif.base_project.data.Session
import id.co.dif.base_project.data.SiteData
import id.co.dif.base_project.data.SiteHistory
import id.co.dif.base_project.data.Skill
import id.co.dif.base_project.data.SparePart
import id.co.dif.base_project.data.TroubleTicket
import id.co.dif.base_project.data.Work
import java.util.Date

class Preferences(val method: PersistenceOperator) {
    private val entities = HashSet<LiveDataPreference<*>>()
    private val preferenceFactory = LiveDataPreferenceFactory(entities)

    fun getEntity(key: String): LiveDataPreference<*>? {
        return entities.firstOrNull() { it.key == key }
    }

    fun wipe() {
        method.deleteAll()
        entities.forEach {
            it.delete()
        }
    }

    val sparePart by lazy {
        preferenceFactory.createPreference<SparePart>(
            "spare_part_data",
            method
        )
    }
    val allSpareParts by lazy {
        preferenceFactory.createPreference<List<SparePart>>(
            "all_spare_part_data",
            method
        )
    }
    val allTicketsDetails by lazy {
        preferenceFactory.createPreference<HashMap<String?, TicketDetails>>(
            "all_tickets_details",
            method
        )
    }
    val loginData by lazy { preferenceFactory.createPreference<LoginData>("login_data", method) }
    val locationUpdateInterval by lazy {
        preferenceFactory.createPreference<Long>(
            "location_update_interval",
            method
        )
    }
    val selectedTechnician by lazy {
        preferenceFactory.createPreference<Location>(
            "selected_technician",
            method
        )
    }
    val session by lazy { preferenceFactory.createPreference<Session>("user_session", method) }
    val myDetailProfile by lazy {
        preferenceFactory.createPreference<BasicInfo>(
            "my_detailed_profile",
            method
        )
    }
    val rcaData by lazy {
        preferenceFactory.createPreference<Rca>(
            "rca_data",
            method
        )
    }
    val employeeList by lazy {
        preferenceFactory.createPreference<List<Employee>>(
            "employee_list",
            method
        )
    }
    val selectedTicketId by lazy {
        preferenceFactory.createPreference<String>(
            "selected_ticket_id",
            method
        )
    }
    val rememberMe by lazy {
        preferenceFactory.createPreference<Boolean>(
            "remember_me",
            method
        )
    }
    val selectedSiteId by lazy {
        preferenceFactory.createPreference<String>(
            "selected_site_id",
            method
        )
    }
    val selectedSite by lazy {
        preferenceFactory.createPreference<Location>(
            "selected_site",
            method
        )
    }
    val ticketDetails by lazy {
        preferenceFactory.createPreference<TicketDetails>(
            "ticket_details",
            method
        )
    }
    val highlightedNote by lazy {
        preferenceFactory.createPreference<Note>(
            "highlighted_note",
            method
        )
    }
    val userEducation by lazy {
        preferenceFactory.createPreference<Education>(
            "user_education",
            method
        )
    }
    val userWork by lazy { preferenceFactory.createPreference<Work>("user_work", method) }
    val skill by lazy { preferenceFactory.createPreference<Skill>("user_skill", method) }
    val selectedProfileId by lazy {
        preferenceFactory.createPreference<Int>(
            "selected_profile_id",
            method
        )
    }
    val requestUpload by lazy {
        preferenceFactory.createPreference<List<Pair<String?, HashMap<String, Any?>>>>(
            "request_upload_batch",
            method
        )
    }
    val firebaseToken by lazy {
        preferenceFactory.createPreference<String>(
            "firebase_token",
            method
        )
    }
    val lastLocation by lazy {
        preferenceFactory.createPreference<LastLocation>(
            "last_location",
            method
        )
    }
    val nextLocationScheduleCommand by lazy {
        preferenceFactory.createPreference<LocationScheduleCommand>(
            "next_location_schedule_command",
            method
        )
    }
    val siteData by lazy { preferenceFactory.createPreference<SiteData>("site_data", method) }
    val lastMapAlarm by lazy {
        preferenceFactory.createPreference<List<Location>>(
            "last_map_alarm",
            method
        )
    }
    val savedEngineers by lazy {
        preferenceFactory.createPreference<List<Location>>(
            "saved_engineers",
            method
        )
    }
    val savedAllSite by lazy {
        preferenceFactory.createPreference<List<Location>>(
            "saved_all_site",
            method
        )
    }
    val listOfNotes by lazy {
        preferenceFactory.createPreference<List<Note>>(
            "list_of_notes",
            method
        )
    }
    val siteHistoryId by lazy {
        preferenceFactory.createPreference<String>(
            "site_history_id",
            method
        )
    }
    val troubleTicket by lazy {
        preferenceFactory.createPreference<TroubleTicket>(
            "trouble_ticket_data",
            method
        )
    }
    val allTroubleTickets by lazy {
        preferenceFactory.createPreference<List<TroubleTicket>>(
            "trouble_ticket_data",
            method
        )
    }
    val myTroubleTickets by lazy {
        preferenceFactory.createPreference<List<TroubleTicket>>(
            "my_trouble_ticket_data",
            method
        )
    }
    val siteHistory by lazy {
        preferenceFactory.createPreference<SiteHistory>(
            "site_history",
            method
        )
    }

    val lastLogin by lazy { preferenceFactory.createPreference<String>("last_login", method) }

}