package util;


import java.io.Serializable;

public class TimerInfo implements Serializable {

        private TimeoutCause cause;
        private Long pollID;
        
        public enum TimeoutCause {
            START, REMIND, END
        }

        public TimerInfo(TimeoutCause cause, Long pollID) {
            this.cause = cause;
            this.pollID = pollID;
        }

        /**
         * @return the cause
         */
        public TimeoutCause getCause() {
            return cause;
        }

        /**
         * @param cause the cause to set
         */
        public void setCause(TimeoutCause cause) {
            this.cause = cause;
        }

        /**
         * @return the pollID
         */
        public Long getPollID() {
            return pollID;
        }

        /**
         * @param pollID the pollID to set
         */
        public void setPollID(Long pollID) {
            this.pollID = pollID;
        }
    }