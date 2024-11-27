package id.co.dif.base_project.data

import com.google.gson.annotations.SerializedName

data class AccountSettings(
    @SerializedName("2nd_email_updated_watson") var swtSecondEmailToReceive: Int = 1,
    @SerializedName("cash_advance") var swtCashAdvanceApprovedOrRejected: Int = 1,
    @SerializedName("claim_paid") var swtExpenseClaimPeriodPaid: Int = 1,
    @SerializedName("new_assign") var swtSendMeEmailWhenNewAssignmentCreateTicketForMe: Int = 1,
    @SerializedName("new_ticket") var swtNewTroubleTicketApproveOrRejected: Int = 1,
    @SerializedName("updated_watson") var swtAnyUpdatedAbout: Int = 1,
    @SerializedName("ticket_closed") var swtTroubleTicketClosed: Int = 1
) {
    fun isIdenticalTo(other: AccountSettings?): Boolean {
        if (other == null) return false
        return swtSecondEmailToReceive == other.swtSecondEmailToReceive
                && swtCashAdvanceApprovedOrRejected == other.swtCashAdvanceApprovedOrRejected
                && swtExpenseClaimPeriodPaid == other.swtExpenseClaimPeriodPaid
                && swtSendMeEmailWhenNewAssignmentCreateTicketForMe == other.swtSendMeEmailWhenNewAssignmentCreateTicketForMe
                && swtNewTroubleTicketApproveOrRejected == other.swtNewTroubleTicketApproveOrRejected
                && swtAnyUpdatedAbout == other.swtAnyUpdatedAbout
                && swtTroubleTicketClosed == other.swtTroubleTicketClosed
    }
}