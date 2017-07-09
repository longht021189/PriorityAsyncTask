package p.a.task.test

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val a = LinkedHashSet<String>()
        a.add("one")
        a.add("two")
        a.add("three")
        a.add("four")
        a.add("fix")
        a.add("six")
        a.add("seven")
        a.add("eight")
        a.add("nine")
        a.add("ten")

        for (i in 0..9) {
            Log.e("DEBUG", a.elementAt(i))
        }
    }
}
