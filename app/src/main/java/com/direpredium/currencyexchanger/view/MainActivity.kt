package com.direpredium.currencyexchanger.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.text.isDigitsOnly
import androidx.room.Room
import com.direpredium.currencyexchanger.R
import com.direpredium.currencyexchanger.databinding.ActivityMainBinding
import com.direpredium.currencyexchanger.model.db.CurrencyExchangerDatabase
import com.direpredium.currencyexchanger.model.db.TransactionRepository
import com.direpredium.currencyexchanger.model.db.WalletRepository
import com.direpredium.currencyexchanger.model.db.entity.Transaction
import com.direpredium.currencyexchanger.model.db.entity.Wallet
import com.direpredium.currencyexchanger.model.network.CurrencyRESTApi
import com.direpredium.currencyexchanger.model.network.CurrencyModel
import com.direpredium.currencyexchanger.model.network.entity.ExchangeRate
import com.direpredium.currencyexchanger.model.network.exception.CustomException
import com.direpredium.currencyexchanger.model.network.retrofit.RetrofitCurrencyRESTApi
import com.direpredium.currencyexchanger.presentor.CurrencyExchangerPresenterImpl
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.MaterialAutoCompleteTextView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.math.BigDecimal
import java.util.Locale

class MainActivity : AppCompatActivity(), CurrencyExchangerView {
    private lateinit var binding: ActivityMainBinding
    private lateinit var presenterImpl: CurrencyExchangerPresenterImpl
    private var isNetworkErrorDialogShowing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.currencyListEdit.setOnItemClickListener {_,_,_,_ ->
            updateConversion()
        }
        binding.convertedCurrencyListEdit.setOnItemClickListener {_,_,_,_ ->
            updateConversion()
        }
        binding.moneyInputEditText.addTextChangedListener(TextFieldChangeListener(::updateConversion))
        binding.moneyInputEditText.addTextChangedListener(TextFieldChangeListener(::validateMoney))
        binding.bConvert.setOnClickListener {
            val baseCurrency = binding.currencyListEdit.text.toString()
            val baseMoney = if(validateMoney()) {binding.moneyInputEditText.text.toString()} else {return@setOnClickListener}
            val targetCurrency = binding.convertedCurrencyListEdit.text.toString()
            if(baseCurrency == targetCurrency || baseMoney.isDigitsOnly()) {
                return@setOnClickListener
            }
            presenterImpl.showConvertDialog(baseCurrency, baseMoney, targetCurrency)
        }
    }

    fun init() {
        val db = Room.databaseBuilder(
            applicationContext,
            CurrencyExchangerDatabase::class.java,
            "currency-exchanger-database"
        ).build()
        presenterImpl = CurrencyExchangerPresenterImpl(this, WalletRepository(db),  TransactionRepository(db), CurrencyModel())
        presenterImpl.loadWallets()
        binding.moneyInputEditText.setText("100.00")
        updateConversion()
    }

    fun sw(string : String) {
        Toast.makeText(this, string, Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        presenterImpl.startUpdatingRates()
    }

    override fun onPause() {
        super.onPause()
        presenterImpl.stopUpdatingRates()
    }

    override fun onDestroy() {
        super.onDestroy()
        presenterImpl.stopUpdatingRates()
    }

    override fun showWallets(wallets: List<Wallet>) {
        val cashAccountData = createCashAccountBundle(wallets)
        val walletFragment = WalletFragment.newInstance(cashAccountData)
        supportFragmentManager.beginTransaction()
            .replace(R.id.frWallet, walletFragment)
            .commit()
    }

    private fun createCashAccountBundle(wallets: List<Wallet>): Bundle {
        return Bundle().apply {
            wallets
                .forEach { wallet ->
                    putDouble(wallet.currency, BigDecimal(wallet.cash).toDouble())
                }
        }
    }

    private fun validateMoney(): Boolean {
        if (binding.moneyInputEditText.text.toString().trim().isEmpty()) {
            binding.moneyInputLayout.error = "Required Field"
            binding.moneyInputEditText.requestFocus()
            return false
        }
        if (!binding.moneyInputEditText.text.toString()
                .matches("^[0-9]+(\\.[0-9]{1,2})?$".toRegex())
        ) {
            binding.moneyInputLayout.error = "Required number"
            binding.moneyInputEditText.requestFocus()
            return false
        }
        binding.moneyInputLayout.isErrorEnabled = false
        return true
    }

    inner class TextFieldChangeListener(private val validating: () -> Boolean) : TextWatcher {
        override fun afterTextChanged(s: Editable?) {}
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            validating()
        }
    }

    override fun updateConversion() : Boolean {
        if (!validateMoney()) {
            return false
        }
        val amount = binding.moneyInputEditText.text.toString().toDouble()
        val baseCurrency = binding.currencyListEdit.text.toString()
        val targetCurrency = binding.convertedCurrencyListEdit.text.toString()
        presenterImpl.loadConvertedMoney(BigDecimal(amount).toString(), baseCurrency, targetCurrency)
        return true
    }

    override fun showConvertedMoney(convertedMoney: BigDecimal) {
        binding.tConvertedMoney.text = String.format(Locale.US, "%.2f", convertedMoney.toDouble())
    }

    override fun showConvertDialog(transaction: Transaction) {
        val commissionString = String.format(Locale.US, "%.2f", BigDecimal(transaction.commission) * BigDecimal(100))
        MaterialAlertDialogBuilder(this)
            .setTitle(resources.getString(R.string.convertdialog_title))
            .setMessage("Convert ${transaction.baseMoney} ${transaction.baseCurrency} to ${transaction.targetMoney} ${transaction.targetCurrency} with a commission of $commissionString%?")
            .setIcon(R.drawable.ic_conversion_24)
            .setNeutralButton(resources.getString(R.string.convertdialog_cancel)) { dialog, which ->

            }
            .setPositiveButton(resources.getString(R.string.convertdialog_accept)) { dialog, which ->
                presenterImpl.convert(transaction)
            }
            .show()
    }

    override fun showNetworkError(ex: CustomException) {
        val progressDialogView = LayoutInflater.from(this).inflate(R.layout.progress_dialog_content, null)

        if(isNetworkErrorDialogShowing) {
            return
        }
        MaterialAlertDialogBuilder(this)
            .setView(progressDialogView)
            .setTitle(resources.getString(R.string.network_error))
            .setMessage(ex.userMessage)
            .setCancelable(false)
            .setPositiveButton(resources.getString(R.string.network_error_accept)) { dialog, which ->
                isNetworkErrorDialogShowing = false
            }
            .show()
        isNetworkErrorDialogShowing = true
    }

    override fun showConvertError(message: String) {

    }

    override fun updateBaseCurrencies(currencies: List<String>) {
        val adapter = ArrayAdapter(this, R.layout.list_item_country, currencies)
        (binding.currencyListLayout.editText as? MaterialAutoCompleteTextView)?.apply {
            setText(currencies.getOrNull(0))
            setAdapter(adapter)
        }
    }

    override fun updateTargetCurrencies(currencies: List<String>) {
        val adapter = ArrayAdapter(this, R.layout.list_item_country, currencies)
        (binding.convertedCurrencyListLayout.editText as? MaterialAutoCompleteTextView)?.apply {
            setText(currencies.getOrNull(0))
            setAdapter(adapter)
        }
    }

}