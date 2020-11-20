package ru.hvost.news.data.api.response

data class GetVouchersResponse(
    val result: String?,
    val error: String?,
    val vouchers: List<Voucher>?
) {
    data class Voucher(
        val voucherCode: String?,
        val petName: String?,
        val petSpecies: String?,
        val expirationDate: String?,
        val voucherProgram: String?
    )
}