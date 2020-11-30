package ru.hvost.news.models

import ru.hvost.news.data.api.response.GetVouchersResponse

sealed class VoucherItem {

    companion object {

        @Suppress("unused")
        fun getTestList() : List<VoucherItem> {
            val result = mutableListOf<VoucherItem>()
            for(i in 0 until 5) {
                result.add(
                    Voucher(
                        id = i.toLong(),
                        voucherCode = "jskjdgflsajdf",
                        petName = "Test",
                        petSpecies = "3",
                        expirationDate = "18.11.1988",
                        voucherProgram = when(i) {
                            0, 1, 2 -> "Первый шаг для щенков"
                            3 -> "Первый шаг для котят"
                            else -> "Стерилизация"
                        }
                    )
                )
            }
            result.add(
                VoucherFooter()
            )
            return result
        }

    }

}

data class Voucher(
    val id: Long,
    val voucherCode: String,
    val petName: String,
    val petSpecies: String,
    val expirationDate: String,
    val voucherProgram: String
): VoucherItem() {

    val textForSpinner = "$voucherProgram - $voucherCode"

}

class VoucherFooter : VoucherItem() {

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun hashCode(): Int {
        return System.identityHashCode(this)
    }

}

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