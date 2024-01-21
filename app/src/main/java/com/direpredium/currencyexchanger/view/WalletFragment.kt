package com.direpredium.currencyexchanger.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.HorizontalScrollView
import android.widget.LinearLayout
import android.widget.Space
import android.widget.TextView
import com.direpredium.currencyexchanger.R

class WalletFragment : Fragment(R.layout.fragment_wallet) {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scrollView: HorizontalScrollView = view.findViewById(R.id.cashAccountScroll)
        val linearLayout: LinearLayout = LinearLayout(requireContext())
        linearLayout.orientation = LinearLayout.HORIZONTAL

        val cashAccount: Bundle? = arguments?.getBundle("cashAccount")
        val accounts: List<Pair<String, Double>> = parseCashAccountBundle(cashAccount)

        for ((currency, amount) in accounts) {
            val accountView = createAccountView(currency, amount)
            linearLayout.addView(accountView)
            linearLayout.addView(Space(requireContext()).apply {
                layoutParams = LinearLayout.LayoutParams(40, ViewGroup.LayoutParams.WRAP_CONTENT)
            })
        }

        scrollView.addView(linearLayout)
    }

    private fun parseCashAccountBundle(cashAccount: Bundle?): List<Pair<String, Double>> {
//        val accounts: MutableList<Account> = mutableListOf()
        val accounts: MutableList<Pair<String, Double>> = mutableListOf()

        cashAccount?.let {
            for (key in it.keySet()) {
                val currency = key
                val amount = it.getDouble(key, 0.0)
//                accounts.add(Account(currency, amount))
                accounts.add(currency to amount)
            }
        }

        return accounts
    }

    private fun createAccountView(currency: String, amount: Double): View {
        val textView = TextView(requireContext())
        textView.text = "$currency: $amount"
        textView.textSize = 23F
        return textView
    }

    companion object {
        @JvmStatic
        fun newInstance(cashAccount : Bundle) =
            WalletFragment().apply {
                arguments = Bundle().apply {
                    putBundle("cashAccount", cashAccount)
                }
            }
    }
}