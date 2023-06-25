@file:OptIn(DelicateCoroutinesApi::class)
package moe.fuqiuluo.net

import io.ktor.network.selector.*
import io.ktor.network.sockets.*
import io.ktor.utils.io.*
import kotlinx.coroutines.*
import moe.fuqiuluo.utils.EMPTY_BYTE_ARRAY
import java.util.concurrent.atomic.AtomicInteger

data class SsoPacket(
    val cmd: String,
    var seq: Int = 0,
    val data: ByteArray = EMPTY_BYTE_ARRAY
)

class SimpleClient(
    private val host: String,
    private val port: Int,
): PacketHandler() {
    private val selectorManager = SelectorManager(Dispatchers.IO)
    private lateinit var socket: Socket

    private lateinit var readChannel: ByteReadChannel
    private lateinit var writeChannel: ByteWriteChannel

    private val seq = AtomicInteger(10000)

    suspend fun connect(): Boolean {
        this.socket = aSocket(selectorManager)
            .tcp()
            .connect(host, port)
        return this.socket.isActive
    }

    fun initConnection() {
        this.readChannel = socket.openReadChannel()
        this.writeChannel = socket.openWriteChannel(true)

        GlobalScope.launch(Dispatchers.IO) {
            while (true) {
                val length = readChannel.readInt() - 4
                if (length > 10 * 1024 * 1024 || length <= 0) error(
                    "the length header of the package must be between 0~10M bytes. data length:" + Integer.toHexString(length)
                )
                readChannel.decode(length) {
                    this@SimpleClient(it)
                }
            }
        }
    }

    fun sendPacket(packet: SsoPacket) {
        if (packet.seq == 0) {
            packet.seq = this.seq.incrementAndGet()
        }
        GlobalScope.launch {
            writeChannel.encode(ssoPacket = packet)
        }
    }
}