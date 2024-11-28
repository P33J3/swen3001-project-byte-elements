import android.graphics.Color
import android.graphics.drawable.Drawable
import android.text.style.ForegroundColorSpan
import androidx.core.content.ContextCompat
import com.prolificinteractive.materialcalendarview.CalendarDay
import com.prolificinteractive.materialcalendarview.DayViewDecorator
import com.prolificinteractive.materialcalendarview.DayViewFacade
import android.content.Context
import com.example.planuslockin.R // Make sure this import is correct

class EventDecorator(
    private val events: List<CalendarDay>,
    private val context: Context
) : DayViewDecorator {

    override fun shouldDecorate(day: CalendarDay?): Boolean {
        return events.contains(day)
    }

    override fun decorate(view: DayViewFacade?) {
        view?.let {
            val eventBackground: Drawable? = ContextCompat.getDrawable(context, R.drawable.event_background)

            eventBackground?.let { drawable ->
                it.setBackgroundDrawable(drawable)
            }

            it.addSpan(ForegroundColorSpan(Color.WHITE))  // Change text color to white
        }
    }
}
