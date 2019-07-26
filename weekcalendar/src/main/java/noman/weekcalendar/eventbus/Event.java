package noman.weekcalendar.eventbus;

import org.joda.time.DateTime;

import java.util.List;

/**
 * Created by nor on 12/5/2015.
 */
public class Event {
    public static class OnDateClickEvent {
        public OnDateClickEvent(DateTime dateTime) {
            this.dateTime = dateTime;
        }

        private DateTime dateTime;

        public DateTime getDateTime() {
            return dateTime;
        }
    }

    public static class InvalidateEvent {
    }


    public static class InterviewEvent {
        public  InterviewEvent(List<String> list)
        {

        }
    }

    public static class UpdateSelectedDateEvent {
        /***
         * Direction -1 for backgroun and 1 for forward
         *
         * @param direction
         */
        public UpdateSelectedDateEvent(int direction) {
            this.direction = direction;
        }

        public int getDirection() {
            return direction;
        }

        private int direction;
    }

    public static class SetCurrentPageEvent {
        public int getDirection() {
            return direction;
        }

        public SetCurrentPageEvent(int direction) {

            this.direction = direction;
        }

        private int direction;
    }

    public static class ResetEvent {
    }

    public static class SetSelectedDateEvent {
        public SetSelectedDateEvent(DateTime selectedDate) {
            this.selectedDate = selectedDate;
        }

        public DateTime getSelectedDate() {
            return selectedDate;
        }

        private DateTime selectedDate;
    }


    public static class SetInteviewDateEvent {

        private List<String> selectedDate;

        public SetInteviewDateEvent(List<String> selectedDate) {
            this.selectedDate = selectedDate;
        }

        public List<String> getSelectedDate() {
            return selectedDate;
        }
    }

    public static class SetStartDateEvent {


        public SetStartDateEvent(DateTime startDate) {
            this.startDate = startDate;
        }

        public DateTime getStartDate() {
            return startDate;
        }

        private DateTime startDate;
    }


}
