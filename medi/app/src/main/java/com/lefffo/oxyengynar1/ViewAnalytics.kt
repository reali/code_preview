package com.lefffo.oxyengynar1

import android.content.Context
import android.util.Log
import com.lefffo.oxyengynar1.data.mediaStatistic
import com.lefffo.oxyengynar1.media.MediaId
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVParser
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.lang3.time.DurationFormatUtils
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

class ViewAnalytics {

    companion object {

        //val list: ArrayList<mediaStatistic> = ArrayList()
        //private val list: MutableList<mediaStatistic> = mutableListOf<mediaStatistic>()
        private val map: MutableMap<String, mediaStatistic> = mutableMapOf<String, mediaStatistic>()
        private var lastSwipeId : MediaId? = 0
        private var lastAdd : Long = 0
        private var lastTitle : String = ""

        private val MIN_SECONDS : Int = 2
        private val FILENAME : String = "views_analytics.csv"

        fun addViewDurations(mediaTitle: String, elapsedSeconds: Long, durationFull: Int, context: Context) {

            if (map.containsKey(mediaTitle)) {

//                map.getValue(mediaTitle).viewsNumber += 1;
                map.getValue(mediaTitle).duration += elapsedSeconds;

                val dur = map.getValue(mediaTitle).duration
                val percentage =  map.getValue(mediaTitle).duration / (durationFull / 100)

                map.getValue(mediaTitle).percentage = percentage

//                Log.e("reali", "$dur $percentage")

//                map.getValue(mediaTitle).percentage =

            } else {
//            list += mediaStatistic(mediaTitle, 1, "/", elapsedSeconds, false)
                val percentage =  elapsedSeconds / (durationFull / 100)

                map.put(mediaTitle, mediaStatistic(mediaTitle, 1, percentage, elapsedSeconds, false))
            }

            writeCSV(context)

        }

        fun resetSwipeId() {
            lastSwipeId = -1
        }

        fun addView(mediaTitle: String, mediaId: MediaId?, context:Context) {

            if (lastSwipeId == mediaId) {

                //Log.e("reali", "!!!!!!!!!!!!!!")
                return
            }

            lastSwipeId = mediaId

            checkSecondsBeforeAddView()

            lastTitle = mediaTitle
            lastAdd = System.currentTimeMillis()

            if (map.containsKey(mediaTitle)) {
                map.getValue(mediaTitle).viewsNumber += 1;
            } else {
                map.put(mediaTitle, mediaStatistic(mediaTitle, 1, 0, 0, false))
            }

            ///

            writeCSV(context)

        }

        private fun writeCSV(context:Context) {

            val path = context.getExternalFilesDir("")
//            val path = Environment.getExternalStorageDirectory()
//            val path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)

            val file = File("$path/$FILENAME")

            val writer = BufferedWriter(FileWriter(file));

            val csvPrinter = CSVPrinter(writer, CSVFormat.DEFAULT
                .withHeader("Title", "Views number", "Percentage", "Duration", "Zoom in"));


            val formatter = SimpleDateFormat("H:mm:ss")
            formatter.timeZone = TimeZone.getTimeZone("UTC");
//            val formatted1 = formatter.format(Date(elapsedSeconds))
//            val formatted = DurationFormatUtils.formatDuration(elapsedSeconds, "H:mm:ss", true)

            for (media in map.values) {
                val mediaStatData = listOf(
                    media.title,
                    media.viewsNumber,
                    media.percentage,
                    DurationFormatUtils.formatDuration(media.duration, "H:mm:ss", true),
                    media.zoom)

                csvPrinter.printRecord(mediaStatData);
            }

            csvPrinter.flush();
            csvPrinter.close();

        }

        private fun checkSecondsBeforeAddView() {

            val end : Long = System.currentTimeMillis()
            val tDeltaMillis: Long = end - lastAdd

            if (end == tDeltaMillis) {
                //Log.e("reali", "first view")
            } else {
                val elapsedSeconds = tDeltaMillis / 1000.0

                if (elapsedSeconds < MIN_SECONDS) {

                    //Log.e("reali", "less than $MIN_SECONDS secs $elapsedSeconds ")

                    //delete last media view
                    if (map.containsKey(lastTitle)) {
                        if (map.getValue(lastTitle).viewsNumber > 0)
                            map.getValue(lastTitle).viewsNumber -= 1;
                    }

                }
            }

        }

        fun exitedMedia(context:Context) {
            checkSecondsBeforeAddView()
            writeCSV(context)
        }

        fun isZoomed(mediaTitle: String) : Boolean {
            if (map.containsKey(mediaTitle) && map.getValue(mediaTitle).zoom)
                return true

            return false
        }

        fun zoomedIn(mediaTitle: String, context:Context) {

            if (map.containsKey(mediaTitle)) {
                if (map.getValue(mediaTitle).zoom)
                    return
                else
                    map.getValue(mediaTitle).zoom = true;
            } else {
//                map.put(mediaTitle, mediaStatistic(mediaTitle, 1, 0, 0, false))
            }

            //Log.e("reali", "zoomedIn")

            writeCSV(context)

        }

        fun reset(context:Context) {

            map.clear()
            writeCSV(context)

        }

        fun read(context:Context) {

            val path = context.getExternalFilesDir("")

            //Log.e("reali", "read ana $path $FILENAME")

//            val path = Environment.getExternalStorageDirectory()
            val file = File("$path/$FILENAME")

            if (!file.exists())
                return

            val reader = BufferedReader(FileReader(file));

            val csvParser = CSVParser(reader, CSVFormat.DEFAULT
                .withFirstRecordAsHeader()
                .withIgnoreHeaderCase()
                .withTrim());

            CSVFormat.DEFAULT
                .withDelimiter(',')
                .withQuote('"')
                .withRecordSeparator("\r\n")

            val formatter = SimpleDateFormat("H:mm:ss")
            formatter.timeZone = TimeZone.getTimeZone("UTC");

            try {

                for (csvRecord in csvParser) {
                    val title = csvRecord.get(0) //csvRecord.get("StudentId");
                    val views = csvRecord.get(1).toInt()
                    val percentage = csvRecord.get(2).toLong()
                    var duration = csvRecord.get(3)
                    var zoom = csvRecord.get(4).toBoolean()
//                println(mediaStatistic(title, views, studentLastName, studentScore))

//                list += mediaStatistic(title, views, percentage, formatter.parse(duration).time, false)
                    map.put(title, mediaStatistic(title, views, percentage, formatter.parse(duration).time, zoom))

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }



//            for (a in list) {
//                val dur = DurationFormatUtils.formatDuration(a.duration, "H:mm:ss", true)
//                Log.e("reali", "${a.title} ${a.viewsNumber} $dur ${a.percentage} ${a.zoom}")
//            }

//            for (a in list) {
//                Log.e("reali", "${a.title} ${a.viewsNumber} ${a.duration} ${a.percentage} ${a.zoom}")
//            }


        }

    }




}