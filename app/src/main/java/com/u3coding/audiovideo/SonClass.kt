package com.u3coding.audiovideo

class SonClass : FatherClass() {
    private var sonName: String? = null
    private var sonAge = 0
    var mSonBirthday: String? = null
    fun printSonMsg() {
        println(
            "Son Msg - name : "
                    + sonName + "; age : " + sonAge
        )
    }

    private fun setSonName(name: String) {
        sonName = name
    }

}
