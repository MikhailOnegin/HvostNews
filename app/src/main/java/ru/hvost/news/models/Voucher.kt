package ru.hvost.news.models

import ru.hvost.news.data.api.response.GetVouchersResponse

data class Voucher(
    val id: Long,
    val voucherCode: String,
    val petName: String,
    val petSpecies: String,
    val expirationDate: String,
    val voucherProgram: String
)

fun GetVouchersResponse.toVouchers(): List<Voucher> {
    return vouchers?.mapIndexed { index, voucher ->
        Voucher(
            id = index.toLong(),
            voucherCode = voucher.voucherCode ?: "",
            petName = voucher.petName ?: "",
            petSpecies = voucher.petSpecies ?: "",
            expirationDate = voucher.expirationDate ?: "",
            voucherProgram = voucher.voucherProgram ?: ""
        )
    } ?: listOf()
}