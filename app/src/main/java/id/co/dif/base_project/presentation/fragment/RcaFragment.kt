package id.co.dif.base_project.presentation.fragment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.ImageView
import android.widget.ListView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.net.toFile
import androidx.core.net.toUri
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.stfalcon.imageviewer.StfalconImageViewer
import id.co.dif.base_project.MAX_IMAGE_SIZE_MEGABYTES
import id.co.dif.base_project.R
import id.co.dif.base_project.base.BaseFragment
import id.co.dif.base_project.data.Location
import id.co.dif.base_project.data.Rca
import id.co.dif.base_project.data.TicketDetails
import id.co.dif.base_project.databinding.FragmentRcaBinding
import id.co.dif.base_project.presentation.dialog.PickerDialog
import id.co.dif.base_project.presentation.fragment.DetailFragment.Companion.CHOOSE_FILE_REQUEST_CODE
import id.co.dif.base_project.presentation.fragment.DetailFragment.Companion.OPEN_CAMERA_REQUEST_CODE
import id.co.dif.base_project.presentation.fragment.DetailFragment.Companion.SELECT_ENGINEER_REQUEST_CODE
import id.co.dif.base_project.presentation.fragment.DetailFragment.Companion.SELECT_SITE_REQUEST_CODE
import id.co.dif.base_project.utils.Resource
import id.co.dif.base_project.utils.StatusCode
import id.co.dif.base_project.utils.colorRes
import id.co.dif.base_project.utils.compressImageFileToDefinedSize
import id.co.dif.base_project.utils.copyImageToFile
import id.co.dif.base_project.utils.findViewsByType
import id.co.dif.base_project.utils.getFileFromUri
import id.co.dif.base_project.utils.getFileNameFromUri
import id.co.dif.base_project.utils.getMimeType
import id.co.dif.base_project.utils.isImageMimeType
import id.co.dif.base_project.utils.isNotNullOrEmpty
import id.co.dif.base_project.utils.loadImage
import id.co.dif.base_project.utils.log
import id.co.dif.base_project.utils.makeMultipartData
import id.co.dif.base_project.utils.megaBytes
import id.co.dif.base_project.utils.orDefault
import id.co.dif.base_project.utils.shimmerDrawable
import id.co.dif.base_project.utils.str
import id.co.dif.base_project.viewmodel.RcaViewModel
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class RcaFragment : BaseFragment<RcaViewModel, FragmentRcaBinding>() {
    override val layoutResId = R.layout.fragment_rca
    var ticketDetails: TicketDetails? = null

    private var imageUri: Uri? = null
    private var currentCategory = ""
    private var currentBefore = ""
    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
    }


    override fun onViewBindingCreated(savedInstanceState: Bundle?) {
        ticketDetails = preferences.ticketDetails.value?.copy()

        if (ticketDetails?.permit_status?.permitApproved == null && ticketDetails?.permit_status?.permitInformation == null && ticketDetails?.tic_field_engineer_id == preferences.myDetailProfile.value?.id.toString()) {
            val listOfEdt = findViewsByType(binding.root, EditText::class.java)
            val listOfImg = findViewsByType(binding.root, ImageView::class.java)
            listOfEdt.forEach {
                it.isEnabled = false
                it.backgroundTintList =
                    ColorStateList.valueOf(R.color.light_grey.colorRes(requireContext()))
            }
            listOfImg.forEach {
                it.isEnabled = false
                it.alpha = 0.5f // Dim the image to indicate disabled state
            }
            binding.idSubmit.isEnabled = false
            binding.idSubmit.backgroundTintList = ColorStateList.valueOf(R.color.light_grey.colorRes(requireContext()))
        }

        viewModel.responseGetRca.observe(viewLifecycleOwner) { response ->
            if (response != null && response.status == 200) { // Periksa status API
                val rca = response.data.list // Ambil detail RCA dari properti list
                rca?.let {
                    print(rca.rca_rh_start_time)
                    binding.rhStartTime.setText(rca.rca_rh_start_time)
                    binding.rhStopTime.setText(rca.rca_rh_stop_time)
                    binding.runningHourStart.setText(rca.rca_rh_start)
                    binding.runningHourStop.setText(rca.rca_rh_stop)
                    binding.responsibleParty.setText(rca.rca_responsible_party)
                    binding.rootcausecategory.setText(rca.rca_rc_category)
                    binding.rootcausetier1.setText(rca.rca_rc_tier1)
                    binding.rootcausetier2.setText(rca.rca_rc_tier2)
                    binding.note.setText(rca.rca_notes)
                    binding.resolutionAction.setText(rca.rca_resolution_action)
                    binding.remark.setText(rca.rca_remarks)
                }
            } else {
                Toast.makeText(context, "Gagal memuat data RCA", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rhStartTime.setOnClickListener {
            showDateTimePicker(binding.rhStartTime)
        }
        binding.rhStopTime.setOnClickListener {
            showDateTimePicker(binding.rhStopTime)
        }
        binding.responsibleParty.setOnClickListener {
            val optionsList = listOf(
                "Telkom",
                "Telkomsel",
                "TI",
                "TP"
            )
            PickerDialog.newInstance(optionsList) { index, value ->
                binding.responsibleParty.setText(value)
                binding.responsibleParty.error = null
                binding.rootcausecategory.text.clear()
                binding.rootcausetier1.text.clear()
                binding.rootcausetier2.text.clear()
                binding.resolutionAction.text.clear()
            }.show(childFragmentManager, getString(R.string.system_fault))
            return@setOnClickListener
        }
        binding.rootcausecategory.setOnClickListener {
            val categoryOptions = when (binding.responsibleParty.text.toString()) {

                "Telkom" -> {
                    arrayListOf("Aktivitas", "Lain Lain", "Transmisi")
                }
                "Telkomsel" -> {
                    arrayListOf("Aktivitas", "Core", "Lain Lain", "Power", "Radio", "Transmisi")
                }
                "TI" -> {
                    arrayListOf("Power")
                }
                "TP" ->{
                    arrayListOf("Power", "Transmisi")
                }
                else -> {
                    arrayListOf()
                }
            }
            PickerDialog.newInstance(categoryOptions) { index, value ->
                binding.rootcausecategory.setText(value)
                binding.rootcausecategory.error = null
                binding.rootcausetier1.text.clear()
                binding.rootcausetier2.text.clear()
                binding.resolutionAction.text.clear()
            }.show(childFragmentManager, getString(R.string.system_fault))
            return@setOnClickListener
        }
        binding.rootcausetier1.setOnClickListener {
            val responsibleParty = binding.responsibleParty.text.toString()
            val rootCauseCategory = binding.rootcausecategory.text.toString()

            val rc1Options = when {
                responsibleParty == "Telkom" && rootCauseCategory == "Aktivitas" -> {
                    arrayListOf("Aktivitas Telkom")
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Lain Lain" -> {
                    arrayListOf("Sarana Penunjang")
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" -> {
                    arrayListOf(
                        "Catu Daya Telkom Rusak",
                        "FO Telkom Putus",
                        "IDR Rusak",
                        "IPMW Telkom Rusak",
                        "ISP FO telkom Rusak",
                        "L2SW Rusak",
                        "Lain Lain",
                        "Metro-E / Small Metro-E Off",
                        "OLT/Mini OLT Rusak",
                        "ONT Rusak"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Aktivitas" -> {
                    arrayListOf("Aktivitas Telkomsel")
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" -> {
                    arrayListOf("Core CS", "Core PS")
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" -> {
                    arrayListOf("Bencana", "Masalah Warga", "Sarana Penunjang")
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" -> {
                    arrayListOf("Baterai", "Genset", "PLN OFF", "Rectifier", "Solar Cell")
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Radio" -> {
                    arrayListOf("BSC")
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Transmisi" -> {
                    arrayListOf("IPMW Telkomsel rusak", "ISP FO Telkomsel Rusak", "OFDM Telkomsel Rusak", "OSP FO Telkomsel Rusak / Putus")
                }
                responsibleParty == "TI" && rootCauseCategory == "Power" -> {
                    arrayListOf("Sewa Daya")
                }
                responsibleParty == "TP" && rootCauseCategory == "Power" -> {
                    arrayListOf("Rectifier/UPS Rusak")
                }
                responsibleParty == "TP" && rootCauseCategory == "Transmisi" -> {
                    arrayListOf("FO TP Putus")
                }
                else -> {
                    arrayListOf()
                }
            }

            if (rc1Options.isNotEmpty()) {
                PickerDialog.newInstance(rc1Options) { index, value ->
                    binding.rootcausetier1.setText(value)
                    binding.rootcausetier1.error = null
                    binding.rootcausetier2.text.clear()
                    binding.resolutionAction.text.clear()
                }.show(childFragmentManager, getString(R.string.system_fault))
            } else {
                Toast.makeText(requireContext(), "Tidak ada opsi tersedia untuk kategori ini.", Toast.LENGTH_SHORT).show()
            }

            return@setOnClickListener
        }
        binding.rootcausetier2.setOnClickListener {
            val responsibleParty = binding.responsibleParty.text.toString()
            val rootCauseCategory = binding.rootcausecategory.text.toString()
            val rootcausetier1 = binding.rootcausetier1.text.toString()

            val rc2Options = when {
                responsibleParty == "Telkom" && rootCauseCategory == "Aktivitas" && rootcausetier1 == "Aktivitas Telkom" -> {
                    arrayListOf("Ada CRQ", "Diluar waktu Aktivitas yang di inginkan", "Melebihi waktu Aktivitas yang di izinkan", "Tidak ada CRQ")
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Sarana Penunjang" -> {
                    arrayListOf("Suhu Panas")
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "Catu Daya Telkom Rusak" -> {
                    arrayListOf("Aki Starter Rusak", "ATS Rusak", "Baterai Rusak", "Genset Rusak/Not running", "MCB rectifier/DCPDB/ACPDB Telkom Trip/Down", "Rectifier Telkom Rusak")
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "FO Telkom Putus" -> {
                    arrayListOf(
                        "Aktivitas pihak ke-3", "Bencana", "Joint Termination (JT) Rusak",
                        "Kualitas FO Jelek", "ODP/ODC Rusak", "Palapa Ring Barat/Tengah/Timur Putus",
                        "Patchcore rusak", "Pencurian/pengrusakan", "SKKL Putus"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "IDR Rusak" -> {
                    arrayListOf(
                        "Catu daya PLN rusak", "Kabel/Konektor/Patchcore Rusak", "Konfigurasi tidak sesuai",
                        "Modul Rusak", "RSL Tidak Sesuai"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "IPMW Telkom Rusak" -> {
                    arrayListOf(
                        "Ceragon Rusak", "IDU Hang",  "Interferensi", "ISR", "Kabel/Konektor/Patchcore Rusak",
                        "Konfigurasi tidak sesuai", "Modul Rusak", "ODU Rusak", "Port/SFP tidak berfungsi",
                        "RSL Tidak Sesuai", "Waveguide/Coaxial rusak"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "ISP FO telkom Rusak" -> {
                    arrayListOf(
                        "ISP hang", "Kabel/Konektor/Patchcore Rusak", "Konfigurasi tidak sesuai", "Modul Rusak",
                        "Port Rusak", "Software tidak sesuai"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "L2SW Rusak" -> {
                    arrayListOf(
                        "Kabel/Konektor/Patchcore Rusak", "L2SW Hang", "Modul Rusak"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "Lain Lain" -> {
                    arrayListOf(
                        "Need further investigation"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "Metro-E / Small Metro-E Off" -> {
                    arrayListOf(
                        "Kabel/Konektor/Patchcore Rusak", "Konfigurasi tidak sesuai", "Metro- E Hang",
                        "Modul Rusak", "Port Rusak", "Software tidak sesuai"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "OLT/Mini OLT Rusak" -> {
                    arrayListOf(
                        "Kabel/Konektor/Patchcore Rusak", "Konfigurasi tidak sesuai", "Modul Rusak",
                        "OLT Hang", "Port Rusak", "Software tidak sesuai"
                    )
                }
                responsibleParty == "Telkom" && rootCauseCategory == "Transmisi" && rootcausetier1 == "ONT Rusak" -> {
                    arrayListOf(
                        "Kabel/Konektor/Patchcore Rusak", "Konfigurasi tidak sesuai", "Modul Rusak",
                        "ONT Hang", "Port Rusak", "Software tidak sesuai"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Aktivitas" && rootcausetier1 == "Aktivitas Telkomsel" -> {
                    arrayListOf(
                        "Ada CRQ", "Diluar waktu Aktivitas yang di inginkan", "Melebihi waktu Aktivitas yang di izinkan", "Tidak ada CRQ"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" -> {
                    arrayListOf(
                        "DSP", "GSC", "GSS", "HLR", "ITP", "MSS", "SBC", "SCP", "STP"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core PS" -> {
                    arrayListOf(
                        "DNS", "DRA", "F5", "GGSN/PGW", "Internet Gate", "SGSN/MME"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Bencana" -> {
                    arrayListOf(
                        "Angin Topan (Tower Roboh)", "Banjir", "Gempa Bumi", "Gunung Meletus", "Kebakaran", "Tanah Longsor"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Masalah Warga" -> {
                    arrayListOf(
                        "Akses ke lokasi ditutup", "Imbas Petir (Perangkat warga)", "Isu LSM", "Perpanjangan Sewa", "Proposal Iuran"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Sarana Penunjang" -> {
                    arrayListOf(
                        "Grounding tidak standar", "Shelter Bocor", "Suhu Panas"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Baterai" -> {
                    arrayListOf(
                        "Baterai Rusak", "Sekering NH Rusak", "Tidak ada Baterai"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" -> {
                    arrayListOf(
                        "Aki Starter Rusak", "Alternator Rusak / Faulty", "ATS Rusak", "BBM Genset habis",
                        "Genset Low Pressure", "Genset Overheat", "Genset Rusak", "Genset Tidak Stabil",
                        "Radiator Bocor"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "PLN OFF" -> {
                    arrayListOf(
                        "ACPDB Rusak", "EAS Tidak Muncul", "Kabel Power Rusak", "KWH Trip",
                        "MCB KWH Trip", "PLN OFF Berulang", "PLN OFF lebih dari 3 jam", "Token Listrik Habis",
                        "Trafo Rusak"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Rectifier" -> {
                    arrayListOf(
                        "Backplane Rusak", "Controller Rusak / Faulty", "I/O Board Rusak",
                        "LVD Rusak / Faulty", "MCB Load Trip / Rusak", "Rectifier Modul Kurang",
                        "Rectifier Modul Rusak"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Solar Cell" -> {
                    arrayListOf(
                        "Baterai Solar Cell Rusak", "Cuaca Buruk", "Kabel/Konektor Rusak",
                        "Modul Rusak", "Panel Surya Rusak"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Radio" && rootcausetier1 == "BSC" -> {
                    arrayListOf(
                        "BSC Not responding", "Kabel/Konektor Rusak", "Konfigurasi tidak sesuai",
                        "Lisensi", "Modul Rusak", "Port/SFP tidak berfungsi", "Software Bugs / Corrupt"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Transmisi" && rootcausetier1 == "IPMW Telkomsel rusak" -> {
                    arrayListOf(
                        "IDU Hang", "Interferensi", "ISR", "Kabel/Konektor/Patchcore Rusak",
                        "Konfigurasi tidak sesuai", "Modul Rusak", "ODU Rusak", "Port/SFP tidak berfungsi",
                        "RSL Tidak Sesuai", "Waveguide/Coaxial rusak"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Transmisi" && rootcausetier1 == "ISP FO Telkomsel Rusak" -> {
                    arrayListOf(
                        "ISP hang", "Kabel/Konektor/Patchcore Rusak", "Konfigurasi tidak sesuai",
                        "Modul Rusak", "Port Rusak", "Software tidak sesuai"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Transmisi" && rootcausetier1 == "OFDM Telkomsel Rusak" -> {
                    arrayListOf(
                        "IDU/POE/LPU Rusak", "Interferensi", "Kabel Rusak", "Konfigurasi tidak sesuai",
                        "ODU Rusak", "OFDM Hang", "RSL Tidak Sesuai"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Transmisi" && rootcausetier1 == "OSP FO Telkomsel Rusak / Putus" -> {
                    arrayListOf(
                        "Aktivitas pihak ke-3", "Bencana", "Kualitas FO Jelek", "Patchcore rusak", "Pencurian/pengrusakan"
                    )
                }
                responsibleParty == "TI" && rootCauseCategory == "Power" && rootcausetier1 == "Sewa Daya" -> {
                    arrayListOf(
                        "BBM Genset Habis", "Genset tidak berfungsi", "Rectifier rusak"
                    )
                }
                responsibleParty == "TP" && rootCauseCategory == "Power" && rootcausetier1 == "Rectifier/UPS Rusak" -> {
                    arrayListOf(
                        "Rectifier/UPS Rusak"
                    )
                }
                responsibleParty == "TP" && rootCauseCategory == "Transmisi" && rootcausetier1 == "FO TP Putus" -> {
                    arrayListOf(
                        "Aktivitas pihak ke-3", "Bencana", "Kualitas FO Jelek", "Patchcore rusak", "Pencurian/pengrusakan"
                    )
                }
                else -> {
                    arrayListOf()
                }
            }

            if (rc2Options.isNotEmpty()) {
                PickerDialog.newInstance(rc2Options) { index, value ->
                    binding.rootcausetier2.setText(value)
                    binding.rootcausetier2.error = null
                    binding.resolutionAction.text.clear()
                }.show(childFragmentManager, getString(R.string.system_fault))
            } else {
                Toast.makeText(requireContext(), "Tidak ada opsi tersedia untuk kategori ini.", Toast.LENGTH_SHORT).show()
            }

            return@setOnClickListener

        }

        binding.resolutionAction.setOnClickListener {
            val responsibleParty = binding.responsibleParty.text.toString()
            val rootCauseCategory = binding.rootcausecategory.text.toString()
            val rootcausetier1 = binding.rootcausetier1.text.toString()
            val rootcausetier2 = binding.rootcausetier2.text.toString()

            val resolutionActionptions = when {
                responsibleParty == "Telkomsel" && rootCauseCategory == "Aktivitas" && rootcausetier1 == "Aktivitas Telkomsel" && rootcausetier2 == "Ada CRQ" -> {
                    arrayListOf(
                        "Menunggu Aktivitas Selesai", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Aktivitas" && rootcausetier1 == "Aktivitas Telkomsel" && rootcausetier2 == "Diluar waktu Aktivitas yang di inginkan" -> {
                    arrayListOf(
                        "Meminta Perpanjangan Waktu CRQ", "Lain Lain",
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Aktivitas" && rootcausetier1 == "Aktivitas Telkomsel" && rootcausetier2 == "Melebihi waktu Aktivitas yang di izinkan" -> {
                    arrayListOf(
                        "Meminta Perpanjangan Waktu CRQ", "Lain Lain",
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Aktivitas" && rootcausetier1 == "Aktivitas Telkomsel" && rootcausetier2 == "Tidak ada CRQ" -> {
                    arrayListOf(
                        "Menunggu Aktivitas Selesai", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "DSP" -> {
                    arrayListOf(
                        "Perbaikan DSP"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "GSC" -> {
                    arrayListOf(
                        "Perbaikan GSC"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "GSS" -> {
                    arrayListOf(
                        "Perbaikan GSS"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "HLR" -> {
                    arrayListOf(
                        "Perbaikan HLR"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "ITP" -> {
                    arrayListOf(
                        "Perbaikan ITP"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "MSS" -> {
                    arrayListOf(
                        "Perbaikan MSS"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "SBC" -> {
                    arrayListOf(
                        "Perbaikan SBC"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "SCP" -> {
                    arrayListOf(
                        "Perbaikan SCP"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core CS" && rootcausetier2 == "STP" -> {
                    arrayListOf(
                        "Perbaikan STP"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core PS" && rootcausetier2 == "DNS" -> {
                    arrayListOf(
                        "Perbaikan DNS"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core PS" && rootcausetier2 == "DRA" -> {
                    arrayListOf(
                        "Perbaikan DRA"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core PS" && rootcausetier2 == "F5" -> {
                    arrayListOf(
                        "Perbaikan F5"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core PS" && rootcausetier2 == "GGSN/PGW" -> {
                    arrayListOf(
                        "Perbaikan GGSN/PGW"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core PS" && rootcausetier2 == "Internet Gate" -> {
                    arrayListOf(
                        "Perbaikan Internet Gate"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Core" && rootcausetier1 == "Core PS" && rootcausetier2 == "SGSN/MME" -> {
                    arrayListOf(
                        "Perbaikan SGSN/MME"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Bencana" && rootcausetier2 == "Angin Topan (Tower Roboh)" -> {
                    arrayListOf(
                        "Menunggu akses ke site dibuka", "Menunggu recovery", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Bencana" && rootcausetier2 == "Banjir" -> {
                    arrayListOf(
                        "Menunggu akses ke site dibuka", "Menunggu recovery", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Bencana" && rootcausetier2 == "Gempa Bumi" -> {
                    arrayListOf(
                        "Menunggu akses ke site dibuka", "Menunggu recovery", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Bencana" && rootcausetier2 == "Gunung Meletus" -> {
                    arrayListOf(
                        "Menunggu akses ke site dibuka", "Menunggu recovery", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Bencana" && rootcausetier2 == "Kebakaran" -> {
                    arrayListOf(
                        "Menunggu akses ke site dibuka", "Menunggu recovery", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Bencana" && rootcausetier2 == "Tanah Longsor" -> {
                    arrayListOf(
                        "Menunggu akses ke site dibuka", "Menunggu recovery", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Masalah Warga" && rootcausetier2 == "Akses ke lokasi ditutup" -> {
                    arrayListOf(
                        "Negosiasi dengan Warga"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Masalah Warga" && rootcausetier2 == "Imbas Petir (Perangkat warga)" -> {
                    arrayListOf(
                        "Eskalasi ke IM (Infrastruktur Management)", "Negosiasi dengan Warga", "Pendataan perangkat terimpact imbas petir", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Masalah Warga" && rootcausetier2 == "Isu LSM" -> {
                    arrayListOf(
                        "Negosiasi dengan LSM", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Masalah Warga" && rootcausetier2 == "Perpanjangan Sewa" -> {
                    arrayListOf(
                        "Negosiasi dengan Warga", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Masalah Warga" && rootcausetier2 == "Proposal Iuran" -> {
                    arrayListOf(
                        "Negosiasi dengan Warga", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Sarana Penunjang" && rootcausetier2 == "Grounding tidak standar" -> {
                    arrayListOf(
                        "Perbaikan Grounding", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Sarana Penunjang" && rootcausetier2 == "Shelter Bocor" -> {
                    arrayListOf(
                        "Perbaikan Shelter", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Lain Lain" && rootcausetier1 == "Sarana Penunjang" && rootcausetier2 == "Suhu Panas" -> {
                    arrayListOf(
                        "Perbaikan AC", "Perbaikan Exhaust Fan", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Baterai" && rootcausetier2 == "Baterai Rusak" -> {
                    arrayListOf(
                        "Mengganti Sekering Baterai", "Menghubungkan dengan Genset", "PLN Sudah Kembali ON", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Baterai" && rootcausetier2 == "Sekering NH Rusak" -> {
                    arrayListOf(
                        "Menghubungkan dengan Genset", "Mengganti Sekering NH", "Menaikan Sekering NH", "PLN Sudah Kembali ON", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Baterai" && rootcausetier2 == "Tidak ada Baterai" -> {
                    arrayListOf(
                        "Menghubungkan dengan Genset", "PLN Sudah Kembali ON", "Menaikan Sekering NH", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "Aki Starter Rusak" -> {
                    arrayListOf(
                        "Mengganti Aki Starter", "Menyalakan Genset secara manual", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "Alternator Rusak / Faulty" -> {
                    arrayListOf(
                        "Pergantian Alternator", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "ATS Rusak" -> {
                    arrayListOf(
                        "Mengganti ATS", "Menyalakan Genset secara manual", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "BBM Genset habis" -> {
                    arrayListOf(
                        "Mengisi BBM", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "Genset Low Pressure" -> {
                    arrayListOf(
                        "Pembersihan Filter", "Penggantian Filter", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "Genset Overheat" -> {
                    arrayListOf(
                        "Pembersihan Filter", "Penggantian Filter", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "Genset Rusak" -> {
                    arrayListOf(
                        "Penggantian perangkat / module genset", "Lain Lain"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "Genset Tidak Stabil" -> {
                    arrayListOf(
                        "Pemasangan AVR (penstabil tegangan)", "Pengecekan tegangan & pergantian perangkat power supply"
                    )
                }
                responsibleParty == "Telkomsel" && rootCauseCategory == "Power" && rootcausetier1 == "Genset" && rootcausetier2 == "Radiator Bocor" -> {
                    arrayListOf(
                        "Penggantian radiator", "Lain Lain"
                    )
                }
                else -> {
                    arrayListOf()
                }
            }


            PickerDialog.newInstance(resolutionActionptions) { index, value ->
                binding.resolutionAction.setText(value)
                binding.resolutionAction.error = null
            }.show(childFragmentManager, getString(R.string.system_fault))
            return@setOnClickListener
        }


        ticketDetails?.let { ticketDetails ->
            binding.idSubmit.setOnClickListener {
                viewModel.submitRca(
                    id = ticketDetails.tic_id,
                    mutableMapOf(
                        "rca_rh_start_time" to binding.rhStartTime.text.toString().orDefault(""),
                        "rca_rh_stop_time" to binding.rhStopTime.text.toString().orDefault(""),
                        "rca_rh_start" to binding.runningHourStart.text.toString().orDefault(""),
                        "rca_rh_stop" to binding.runningHourStop.text.toString().orDefault(""),
                        "rca_responsible_party" to binding.responsibleParty.text.toString().orDefault(""),
                        "rca_rc_category" to binding.rootcausecategory.text.toString().orDefault(""),
                        "rca_rc_tier1" to binding.rootcausetier1.text.toString().orDefault(""),
                        "rca_rc_tier2" to binding.rootcausetier2.text.toString().orDefault(""),
                        "rca_notes" to binding.note.text.toString().orDefault(""),
                        "rca_resolution_action" to binding.resolutionAction.text.toString().orDefault(""),
                        "rca_remarks" to binding.remark.text.toString().orDefault("")
                    )
                ).log("kondisi terima")
            }
        }

        viewModel.responseSubmitRca.observe(lifecycleOwner) {
            if (it.status == 200) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                viewModel.getRca(ticketDetails?.tic_id)
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.responseSubmitRcaPhoto.observe(lifecycleOwner) {
            if (it.status == 200) {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                requireActivity().supportFragmentManager.popBackStack()
                viewModel.getRcaImage(ticketDetails?.tic_id)
            } else {
                Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.responseGetRcaImage.observe(viewLifecycleOwner) { response ->
            "awal 12354".log(response.toString())
            response?.let {
                if (it.status == 200) {
                    it.data?.list?.forEach { item ->
                        when (item.category) {
                            "RUNNING HOUR" -> {
                                "running hour 12345".log(item.photo)
                                if (item.label == "BEFORE") {
                                    binding.imgRunningHourStart.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgRunningHourStart.loadImage(item.photo, shimmerDrawable())

                                } else if (item.label == "AFTER") {
                                    binding.imgRunningHourStop.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgRunningHourStop.loadImage(item.photo, shimmerDrawable())
                                }
                            }
                            "KWH FOTO" -> {
                                "maintean progrees 12345".log(item.photo)
                                if (item.label == "BEFORE") {
                                    binding.imgKwhFotoBefore.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgKwhFotoBefore.loadImage(item.photo, shimmerDrawable())
                                } else if (item.label == "AFTER") {
                                    binding.imgKwhFotoAfter.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgKwhFotoAfter.loadImage(item.photo, shimmerDrawable())
                                }
                            }
                            "RECTIFIER" -> {
                                "maintean progrees 12345".log(item.photo)
                                if (item.label == "BEFORE") {
                                    binding.imgRectifierBefore.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgRectifierBefore.loadImage(item.photo, shimmerDrawable())
                                } else if (item.label == "AFTER") {
                                    binding.imgRectifierAfter.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgRectifierAfter.loadImage(item.photo, shimmerDrawable())
                                }
                            }
                            "MAINTENANCE PROGRES" -> {
                                "maintean progrees 12345".log(item.photo)
                                if (item.label == "BEFORE") {
                                    binding.imgMaintenanceProgressBefore.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgMaintenanceProgressBefore.loadImage(item.photo, shimmerDrawable())
                                } else if (item.label == "AFTER") {
                                    binding.imgMaintenanceProgressAfter.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgMaintenanceProgressAfter.loadImage(item.photo, shimmerDrawable())
                                }
                            }
                            "BTS FOTO" -> {
                                "bts foto 12345".log(item.photo)
                                if (item.label == "BEFORE") {
                                    binding.imgBtsBefore.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgBtsBefore.loadImage(item.photo, shimmerDrawable())
                                } else if (item.label == "AFTER") {
                                    binding.imgBtsAfter.setOnClickListener {
                                        StfalconImageViewer.Builder<String>(context, arrayOf(item.photo)) { view, image ->
                                            Picasso.get().load(image).into(view)
                                        }.show()
                                    }
                                    binding.imgBtsAfter.loadImage(item.photo, shimmerDrawable())
                                }
                            }
                        }
                    }
                } else {
                    showError(it.message ?: "Failed to fetch data")
                }
            }
        }


        setupClickListeners()


    }

    fun showSuccessMessage(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    private fun showDateTimePicker(editText: EditText) {
        val calendar = Calendar.getInstance()
        val datePicker = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                calendar.set(year, month, dayOfMonth)
                val timePicker = TimePickerDialog(
                    requireContext(),
                    { _, hourOfDay, minute ->
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay)
                        calendar.set(Calendar.MINUTE, minute)
                        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
                        val selectedDateTime = dateTimeFormat.format(calendar.time)
                        editText.setText(selectedDateTime)
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePicker.show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePicker.show()
    }

    override fun onResume() {
        super.onResume()
        val id = ticketDetails?.tic_id
        viewModel.getRca(id)
        viewModel.getRcaImage(id)
    }

    private fun setupClickListeners() {
        binding.imgRunningHourStart.setOnClickListener {
            currentCategory = "RUNNING HOUR"
            currentBefore = "BEFORE"
            openGallery()
        }

        binding.imgRunningHourStop.setOnClickListener {
            currentCategory = "RUNNING HOUR"
            currentBefore = "AFTER"
            openGallery()
        }

        binding.imgKwhFotoBefore.setOnClickListener {
            currentCategory = "KWH FOTO"
            currentBefore = "BEFORE"
            openGallery()
        }

        binding.imgKwhFotoAfter.setOnClickListener {
            currentCategory = "KWH FOTO"
            currentBefore = "AFTER"
            openGallery()
        }

        binding.imgRectifierBefore.setOnClickListener {
            currentCategory = "RECTIFIER"
            currentBefore = "BEFORE"
            openGallery()
        }
        binding.imgRectifierAfter.setOnClickListener {
            currentCategory = "RECTIFIER"
            currentBefore = "AFTER"
            openGallery()
        }
        binding.imgBtsBefore.setOnClickListener {
            currentCategory = "BTS FOTO"
            currentBefore = "BEFORE"
            openGallery()
        }
        binding.imgBtsAfter.setOnClickListener {
            currentCategory = "BTS FOTO"
            currentBefore = "AFTER"
            openGallery()
        }
        binding.imgMaintenanceProgressBefore.setOnClickListener {
            currentCategory = "MAINTENANCE PROGRES"
            currentBefore = "BEFORE"
            openGallery()
        }
        binding.imgMaintenanceProgressBefore.setOnClickListener {
            currentCategory = "MAINTENANCE PROGRES"
            currentBefore = "AFTER"
            openGallery()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == AppCompatActivity.RESULT_OK && data != null) {
            when (requestCode) {
                REQUEST_IMAGE_PICK -> {
                    lifecycleScope.launch {
                        val fileUri = data.data
                        fileUri?.let { fileUri ->
                            val file = requireContext().getFileFromUri(requireContext(), fileUri)
                            val fileSize = java.lang.String.valueOf(file.length() / 1024).toInt()
                            val mimeType = getMimeType(currentActivity, fileUri)
                            Log.d("TAG", "FileUpload - File Size : $fileSize")
                            Log.d("TAG", "FileUpload - Mime Type : $mimeType")
                            if (mimeType?.startsWith("video") == true && fileSize > (50 * 1024)) {
                                showToast("The video can't be more than 50Mb !")
                                return@let
                            } else if (mimeType?.startsWith("application/pdf") == true && fileSize > (10 * 1024)) {
                                showToast("The pdf can't be more than 10Mb !")
                                return@let
                            }
                            val isImage = isImageMimeType(mimeType)
                            var uri = fileUri
                            if (isImage) {
                                uri = currentActivity.copyImageToFile(uri).toUri()
                                uri = currentActivity.compressImageFileToDefinedSize(MAX_IMAGE_SIZE_MEGABYTES.megaBytes, uri).toUri()
                            }
                            viewModel.file = uri.let { requireContext().getFileFromUri(requireContext(), uri) }

                            val params = hashMapOf<String, Any?>(
                                "rcaimg_category" to currentCategory,
                                "rcaimg_before" to currentBefore,
                                "rca_image" to viewModel.file
                            )
                            viewModel.submitRcaPhoto(
                                ticketDetails?.tic_id,
                                params.map { makeMultipartData(it.key, it.value) }.toMutableList()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*" // Anda bisa mengganti ke `*/*` jika ingin memungkinkan tipe file lainnya.
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    private fun showError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }



}