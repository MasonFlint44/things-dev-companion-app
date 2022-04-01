package things.dev.features.wifilogin.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import things.dev.R
import things.dev.features.wifi.framework.models.WifiFreq
import things.dev.features.wifi.framework.models.WifiLevel
import things.dev.features.wifi.framework.models.WifiSecurity
import javax.inject.Inject

class WifiLoginFragment @Inject constructor() : Fragment() {
    private val viewModel: WifiLoginViewModel by activityViewModels()
    private lateinit var passwordText: TextView
    private lateinit var scanResultText: TextView
    private lateinit var scanResultFreq: TextView
    private lateinit var scanResultLevel: ImageView
    private lateinit var loadingSpinner: ProgressBar

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel.scanResult.observe(viewLifecycleOwner, {
            scanResultText.text = it.ssid
            scanResultFreq.text = when(it.freq) {
                WifiFreq.FREQ_2_4_GHZ -> "2.4 GHz"
                WifiFreq.FREQ_5_GHZ -> "5 GHz"
                else -> "??? GHz"
            }

            val wifiLevelDrawable = when {
                it.level == WifiLevel.NONE ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_0_bar_24, null)
                it.level == WifiLevel.WEAK && it.security != WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_1_bar_lock_24, null)
                it.level == WifiLevel.FAIR && it.security != WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_2_bar_lock_24, null)
                it.level == WifiLevel.GOOD && it.security != WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_3_bar_lock_24, null)
                it.level == WifiLevel.EXCELLENT && it.security != WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_4_bar_lock_24, null)
                it.level == WifiLevel.WEAK && it.security == WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_1_bar_24, null)
                it.level == WifiLevel.FAIR && it.security == WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_2_bar_24, null)
                it.level == WifiLevel.GOOD && it.security == WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_3_bar_24, null)
                it.level == WifiLevel.EXCELLENT && it.security == WifiSecurity.OPEN ->
                    ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_4_bar_24, null)
                else -> ResourcesCompat.getDrawable(requireActivity().resources, R.drawable.twotone_signal_wifi_0_bar_24, null)
            }
            scanResultLevel.setImageDrawable(wifiLevelDrawable)
        })
        viewModel.loading.observe(viewLifecycleOwner, {
            when(it) {
                true -> {
                    loadingSpinner.visibility = View.VISIBLE
                    scanResultLevel.visibility = View.GONE
                }
                else -> {
                    loadingSpinner.visibility = View.GONE
                    scanResultLevel.visibility = View.VISIBLE
                }
            }
        })

        return inflater.inflate(R.layout.wifi_login_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        passwordText = view.findViewById(R.id.passwordText)
        scanResultText = view.findViewById(R.id.scanResultText)
        scanResultFreq = view.findViewById(R.id.scanResultFreq)
        scanResultLevel = view.findViewById(R.id.scanResultLevel)
        loadingSpinner = view.findViewById(R.id.loadingSpinner)
        
        passwordText.doOnTextChanged { text, start, count, after ->
            viewModel.password.value = text.toString()
        }
    }
}