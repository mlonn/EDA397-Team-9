package se.chalmers.eda397.team9.cardsagainsthumanity.ViewClasses;

public class Message {
    public static class Type {
        public static final String REQUEST_ALL_TABLES = "request_all_tables";
        public static final String REQUEST_TABLE = "request_table";
        public static final String PLAYER_JOIN_REQUEST = "player_join_request";
        public static final String PLAYER_INTERVAL_UPDATE = "player_interval_update";
        public static final String GAME_STARTED = "game_started";
        public static final String START_REFRESHING = "start_refreshing";
        public static final String STOP_REFRESHING = "stop_refreshing";
        public static final String SPINNER_UPDATE_TABLE = "spinner_update_table" ;
        public static final String NO_RESPONSE = "no_response";
        public static final String PLAYER_TIMED_OUT = "player_timed_out";
        public static final String TABLE_FULL = "table_full";
        public static final String MY_DEVICE_ADDRESS_FOUND = "my_device_address_found";
        public static final String SUBMISSION = "submission";
        public static final String SELECTED_WINNER = "selected_winner";
        public static final String STOPPED_SENDING = "stopped_sending";
    }
    public static class Response {
        public static final String HOST_TABLE = "host_table_info";
        public static final String PLAYER_JOIN_ACCEPTED = "player_join_accepted";
        public static final String PLAYER_JOIN_DENIED = "player_join_denied";
        public static final String PLAYER_JOIN_CONFIRM = "table_join_confirm";
        public static final String PLAYER_DISCONNECTED = "player_disconnected";
        public static final String OTHER_PLAYER_JOIN_ACCEPTED = "other_player_join_accepted";
        public static final String SELF_PLAYER_JOIN_ACCEPTED = "self_player_join_accepted";
        public static final String GAME_START_CONFIRMED = "game_start_confirmed";
        public static final String ALL_CONFIRMED = "all_confirmed";
        public static final String GAME_START_DENIED = "game_start_denied";
        public static final String RECEIVED_SUBMISSION = "received_submission";
        public static final String RECEIVED_WINNER = "received_winner";
    }
    public static class Target {
        public static final String ALL_DEVICES = "all_devices";
    }
}
