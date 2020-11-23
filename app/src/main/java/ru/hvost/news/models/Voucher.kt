package ru.hvost.news.models

import ru.hvost.news.data.api.response.GetVouchersResponse

sealed class VoucherItem

data class Voucher(
    val id: Long,
    val voucherCode: String,
    val petName: String,
    val petSpecies: String,
    val expirationDate: String,
    val voucherProgram: String
): VoucherItem()

class VoucherFooter : VoucherItem()

fun GetVouchersResponse.toVouchers(): List<VoucherItem> {
    val result = mutableListOf<VoucherItem>()
    val vouchers = vouchers?.mapIndexed { index, voucher ->
        Voucher(
            id = index.toLong(),
            voucherCode = voucher.voucherCode ?: "",
            petName = voucher.petName ?: "",
            petSpecies = voucher.petSpecies ?: "",
            expirationDate = voucher.expirationDate ?: "",
            voucherProgram = voucher.voucherProgram ?: ""
        )
    } ?: listOf()
    result.addAll(vouchers)
    result.add(
        VoucherFooter()
    )
    return result
}