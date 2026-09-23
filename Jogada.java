// import java.util.List;

public enum Jogada {
    PEDRA {
        @Override 
        public boolean ganhaDe(Jogada jogadaAdversaria) {
            if (jogadaAdversaria.equals(TESOURA) || jogadaAdversaria.equals(LAGARTO)) {
                return true;
            }
            return false;
        }
    },
    PAPEL {
        @Override 
        public boolean ganhaDe(Jogada jogadaAdversaria) {
            if (jogadaAdversaria.equals(PEDRA) || jogadaAdversaria.equals(SPOCK)) {
                return true;
            }
            return false;
        }
    },
    TESOURA {
        @Override 
        public boolean ganhaDe(Jogada jogadaAdversaria) {
            if (jogadaAdversaria.equals(PAPEL) || jogadaAdversaria.equals(LAGARTO)) {
                return true;
            }
            return false;
        }
    },
    LAGARTO {
        @Override 
        public boolean ganhaDe(Jogada jogadaAdversaria) {
            if (jogadaAdversaria.equals(PAPEL) || jogadaAdversaria.equals(SPOCK)) {
                return true;
            }
            return false;
        }
    },
    SPOCK {
        @Override 
        public boolean ganhaDe(Jogada jogadaAdversaria) {
            if (jogadaAdversaria.equals(TESOURA) || jogadaAdversaria.equals(PEDRA)) {
                return true;
            }
            return false;
        }
    };

    public abstract boolean ganhaDe(Jogada jogadaAdversaria);
}