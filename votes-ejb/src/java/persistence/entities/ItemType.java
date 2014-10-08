package persistence.entities;

public enum ItemType {
	YES_NO("poll.items.type.yesNo"),
	ONE_OF_N("poll.items.type.oneOfN"),
	M_OF_N("poll.items.type.mOfN");
        
        private String label;

        private ItemType(String label) {
            this.label = label;
        }

        public String getLabel() {
            return label;
        }
        
}

    
