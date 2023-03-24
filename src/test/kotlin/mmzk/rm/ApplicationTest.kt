package mmzk.rm

import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.testing.*
import kotlinx.serialization.json.Json
import mmzk.rm.models.*
import kotlin.test.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
        }
    }

    @Test
    fun canEncodeEmptyList() = testEncodeList(listOf(), EncodeNum(false, "0"))

    @Test
    fun canEncodeSingletonList() = testEncodeList(listOf("10"), EncodeNum(false, "1024"))

    @Test
    fun canEncodeList() = testEncodeList(listOf("3", "5", "2"), EncodeNum(false, "4616"))

    @Test
    fun doNotEncodeBigList() = testEncodeList(listOf("1919810", "114514"), EncodeNum(true))

    @Test
    fun canEncodeRM() = testEncodeRM(
        """
            1- 1 2
            0+ 0
        """.trimIndent(),
        listOf(EncodeNum(false, "152"), EncodeNum(false, "1")),
        EncodeNum(false, "28544953854119197621165719388989902727654932480")
    )

    @Test
    fun doNotEncodeLargeRM() = testEncodeRM(
        """
            checkIs0: R1- checkIs1 end
            checkIs1: R1- addBack1 reach1
            addBack1: R1+ addBack2
            addBack2: R1+ mod0
            mod0:     R1- mod1     R2ToR1
            mod1:     R1- div2     odd
            div2:     R2+ mod0
            R2ToR1:   R2- R1FromR2 countInc
            R1FromR2: R1+ R2ToR1
            countInc: R0+ checkIs0
            odd:      R1+ clearR2
            clearR2:  R2- R1Reset1 R1To3R2
            R1Reset1: R1+ R1Reset2
            R1Reset2: R1+ clearR2
            R1To3R2:  R1- times3_1 R1Plus1
            times3_1: R2+ times3_2
            times3_2: R2+ times3_3
            times3_3: R2+ R1To3R2
            R1Plus1:  R1+ dumpR2
            dumpR2:   R2- fillR1   countInc
            fillR1:   R1+ dumpR2
            reach1:   R0+ end
            end:      H
        """.trimIndent(),
        listOf(
            EncodeNum(false, "1432"),
            EncodeNum(false, "2744"),
            EncodeNum(false, "28"),
            EncodeNum(false, "36"),
            EncodeNum(false, "7672"),
            EncodeNum(false, "21496"),
            EncodeNum(false, "144"),
            EncodeNum(false, "311264"),
            EncodeNum(false, "60"),
            EncodeNum(false, "1"),
            EncodeNum(false, "92"),
            EncodeNum(false, "7602144"),
            EncodeNum(false, "108"),
            EncodeNum(false, "92"),
            EncodeNum(false, "19398648"),
            EncodeNum(false, "528"),
            EncodeNum(false, "560"),
            EncodeNum(false, "464"),
            EncodeNum(false, "156"),
            EncodeNum(false, "1275068384"),
            EncodeNum(false, "156"),
            EncodeNum(false, "45"),
            EncodeNum(false, "0"),


            ),
        EncodeNum(true)
    )

    @Test
    fun canDetectEncodeSyntaxError() = testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        client.post("/encode") {
            contentType(ContentType.Application.Json)
            setBody(EncodeRequest(code = "FOOBAR"))
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            val data = Json.decodeFromString(EncodeResponse.serializer(), bodyAsText())
            assertTrue(data.hasError)
        }
    }

    @Test
    fun canDecodeZero() = testDecode("0", null, listOf(), "")

    @Test
    fun canDecodeAdder() = testDecode(
        "28738678589325274522871087916445314772282785417214007261208231566195210827554169179139861628470962486606050497613789319537928452929252379720353942732879195765819929905481411192367913165424870405419359236629869563732570138212258934001178960300833861066045972985771779328041376477915028074641341742845112134412602756554506347740596379378771007129255295254260103608448723007616643684771274023685388049424268528133557469406988031448736465728547080432626258098667281737687908406236777938345714191323532562315600695931011610336637664954962548791057158591283473247357043382114632403895483000116092134518686862114381002254085074428592896626333315826705672990675973186146242892276056650679867491893573492116998932363973633639087766413428595732271832274448318088427381471021785644268820953877626134901789547297676215103641013574133336623969712045882654242431727896093194490955173751425648027501827221333805372761355399652661789738833048217909906861836304228703616513335888646946990427220815196181512059681627100470476690600390128221682196050367210088188662695436612748023048427226148698151404842535621340206218031127722370326842030151162040144267846930945521052830263468727196374740202910097556253633281843875156784405312440341158883729372819819334058400746019743922715399937828161817022670556978440727174928369172154273083243857404267686720588699632647990306415126457193269688351591566128567521220764583547861786591559018936249608044544",
        listOf(
            "152",
            "2516966635871625461680045307025542831014053893796973358975862174007211185928485880658201083005266549589276847323066925940072264003132134897273770261599534951193712458670026357541887530253318849511481687961385885948825346794642000061451860192493695927439461289142056660820821562100253138443432933695687171126952676761449837310632861169323075533427547289965805640424170653361782249805621498850978155155890253847137417072119964436570088099843803234894889232414830425893247838019798515681776795670633826186937677519914480816826837553798548063558007680899843330199702184785776816367461471971489547980584557949480566939455005802449154782412108918650206589513403371655062781631563794637387001494333115841374848732927747689063958054044864679587054327203660911327199202309344325602377133502557284509080210828600777097232835562172736891837573556846166575012713317445978869029017325357767504495771841907693777723323655607058627119296611810437164231442825126734249781848515383614987579081046977936100047769392268598505125845222029071321775738533909422567906976320411017484404528358761486702345303019111451822877724394891265345212065891275554928825461643230669333975211312599717523087916996512858456893211058537666339220019865117644911833665823182394171928834052723451888153227611849706056828712303089201192459952408866196363491936573812728993172645642631493099710816799053793676731672525012994"
        ),
        listOf("152", "1", "4576", "5", "0"),
        """
            L0: R1- 1 2
            L1: R0+ 0
            L2: R2- 3 4
            L3: R0+ 2
            L4: ARRÊT
        """.trimIndent()
    )

    @Test
    fun canDetectDecodeSyntaxError() = testApplication {
        val client = createClient {}
        client.post("/decode") {
            contentType(ContentType.Application.Json)
            setBody("-1919810")
        }.apply {
            assertEquals(HttpStatusCode.BadRequest, status)
            val data = Json.decodeFromString(DecodeResponse.serializer(), bodyAsText())
            assertTrue(data.hasError)
        }
    }

    @Test
    fun canAddFiveToSeven() = testSimulate(
        code = """
            L0: R1- 1 2
            L1: R0+ 0
            L2: R2- 3 4
            L3: R0+ 2
            L4: ARRÊT
        """.trimIndent(),
        args = listOf("7", "5"),
        expectedSteps = 27,
        expectedRegValues = listOf("12", "0", "0")
    )

    @Test
    fun canAddFiveToSevenToTen() = testSimulate(
        code = """
            L0: R1- 1 2
            L1: R0+ 0
            L2: R2- 3 4
            L3: R0+ 2
            L4: ARRÊT
        """.trimIndent(),
        args = listOf("10", "7", "5"),
        startFromR0 = true,
        expectedSteps = 27,
        expectedRegValues = listOf("22", "0", "0")
    )

    private fun testEncodeList(list: List<String>, expected: EncodeNum) = testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        client.post("/encode") {
            contentType(ContentType.Application.Json)
            setBody(EncodeRequest(args = list))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val data = Json.decodeFromString(EncodeResponse.serializer(), bodyAsText())
            assertFalse(data.hasError)
            assertEquals(expected, data.encodeFromList)
        }
    }

    private fun testEncodeRM(code: String, expectedList: List<EncodeNum>, expectedGodel: EncodeNum) = testApplication {
        val client = createClient {
            this.install(ContentNegotiation) {
                json()
            }
        }
        client.post("/encode") {
            contentType(ContentType.Application.Json)
            setBody(EncodeRequest(code = code))
        }.apply {
            assertEquals(HttpStatusCode.OK, status)
            val data = Json.decodeFromString(EncodeResponse.serializer(), bodyAsText())
            assertFalse(data.hasError)
            assertEquals(expectedGodel, data.encodeFromRM)
            assertEquals(expectedList, data.encodeToLine)
        }
    }

    private fun testDecode(num: String, expectedPair: List<String>?, expectedList: List<String>, expectedRM: String) =
        testApplication {
            val client = createClient {}
            client.post("/decode") {
                setBody(num)
            }.apply {
                assertEquals(HttpStatusCode.OK, status)
                val data = Json.decodeFromString(DecodeResponse.serializer(), bodyAsText())
                assertFalse(data.hasError)
                assertEquals(data.decodeToRM, expectedRM)
                assertEquals(data.decodeToList, expectedList)
                assertEquals(data.decodeToPair, expectedPair)
            }
        }

    private fun testSimulate(
        code: String,
        args: List<String>,
        startFromR0: Boolean = false,
        expectedSteps: Int,
        expectedRegValues: List<String>
    ) =
        testApplication {
            val client = createClient {
                this.install(ContentNegotiation) {
                    json()
                }
            }
            client.post("/simulate") {
                contentType(ContentType.Application.Json)
                setBody(SimulateRequest(code = code, args = args, startFromR0 = startFromR0))
            }.apply {
                println(Json.decodeFromString(SimulateResponse.serializer(), bodyAsText()))
                assertEquals(HttpStatusCode.OK, status)
                val data = Json.decodeFromString(SimulateResponse.serializer(), bodyAsText())
                assertFalse(data.hasError)
                assertEquals(expectedSteps, data.steps)
                assertEquals(expectedRegValues, data.registerValues)
            }
        }
}
