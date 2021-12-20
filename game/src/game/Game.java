package game;

import board.Board;
import board.Colony;
import board.Road;
import board.Tile;
import vue.Vues;

import java.util.ArrayList;

public class Game{
    private Player[] players;
    private Board board;
    private Vues vueGenerale;

    public Game(){
        board=new Board();
        players=new Player[4];
    }

    // fonction construisant une route pour un joueur aux coordonnées en argument
    public boolean buildRoad(int line,int column,int roadNumber,Player player){
        Road choosedRoad=board.getTiles()[line][column].getRoads().get(roadNumber);
        if(choosedRoad.isOwned()){
            return false;
        }
        int clayStock=player.ressources.get("Clay");
        int woodStock=player.ressources.get("Wood");
        if(clayStock>=1 && woodStock>=1){
            if(player.canBuildPropertie("Road",15)){
                if(choosedRoad.isBuildable(player)){
                    choosedRoad.setPlayer(player);
                    player.ressources.replace("Clay", clayStock-1);
                    player.ressources.replace("Wood", woodStock-1);
                    player.addPropertie("Road");
                    return true;
                }
                else{
                    System.out.println("Vous ne pouvez pas constuire de route ici.");
                }
            }
            else{
                System.out.println("Vous ne pouvez pas constuire de route, vous avez atteint la quantité maximum possible.");
            }
        }
        else{
            System.out.println("Vous n'avez pas la quantité suffisante de ressources pour constuire une route.");
        }
        return false;
    }

    // fonction construisant une colonie pour un joueur aux coordonnées en argument.
    // si la colonie est un port il est ajouté au hashmap du joueur
    public boolean buildColony(int line,int column,int colonyNumber,Player player){
        Colony choosedColony=board.getTiles()[line][column].getColonies().get(colonyNumber);
        if(choosedColony.isOwned()){
            return false;
        }
        int clayStock=player.ressources.get("Clay");
        int wheatStock=player.ressources.get("Wheat");
        int woodStock=player.ressources.get("Wood");
        int woolStock=player.ressources.get("Wool");
        if(clayStock>=1 && wheatStock>=1 && woodStock>=1 && woolStock>=1){
            if(player.canBuildPropertie("Colony",5)){
                if(choosedColony.isBuildable(player)){
                    if(choosedColony.isPort()){
                        player.addPort(choosedColony.getLinkedPort());
                    }
                    choosedColony.setPlayer(player);
                    player.ressources.replace("Clay",clayStock-1);
                    player.ressources.replace("Wheat",wheatStock-1);
                    player.ressources.replace("Wood",woodStock-1);
                    player.ressources.replace("Wool",woolStock-1);
                    player.addPropertie("Colony");
                    player.addVictoryPoint(1);
                    return true;
                }
                else{
                    System.out.println("Vous ne pouvez pas constuire de colonie ici.");
                }
            }
            else{
                System.out.println("Vous ne pouvez pas constuire de colonie, vous avez atteint la quantité maximum possible.");
            }
        }
        else{
            System.out.println("Vous n'avez pas la quantité suffisante de ressources pour constuire une colonie.");
        }
        return false;
    }

    // fonction construisant une ville pour un joueur aux coordonnées en argument
    public boolean buildCity(int line,int column,int colonyNumber,Player player){
        Colony choosedColony=board.getTiles()[line][column].getColonies().get(colonyNumber);
        if(!choosedColony.isOwned(player)){
            return false;
        }
        int oreStock=player.ressources.get("Ore");
        int wheatStock=player.ressources.get("Wheat");
        if(wheatStock>=2 && oreStock>=3){
            if(player.canBuildPropertie("City",4)){
                if(choosedColony.isOwned(player)){
                    choosedColony.setAsCity();
                    player.ressources.replace("Wheat",wheatStock-2);
                    player.ressources.replace("Ore",oreStock-3);
                    player.addPropertie("City");
                    player.removeColonyInCounter();
                    player.addVictoryPoint(2);
                    return true;
                }
                else{
                    System.out.println("La colonie ne vous appartient pas, vous ne pouvez pas constuire de ville ici.");
                }
            }
            else{
                System.out.println("Vous ne pouvez pas constuire de ville, vous avez atteint la quantité maximum possible.");
            }
        }
        else{
            System.out.println("Vous n'avez pas la quantité suffisante de ressources pour constuire une ville.");
        }
        return false;
    }

    // fonction donnant aux joueurs les ressources produites si leur colonie se trouve sur la case de l'id en argument
    public void diceProduction(int diceNumber){
        for(Tile[] tiles:board.getTiles()){
            for(Tile tile:tiles){
                if(tile.getId()==diceNumber){
                    String ressource=tile.getRessource();
                    for(Colony colony:tile.getColonies()){
                        int producedValue=colony.isCity()?2:1;
                        colony.getPlayer().propertiesCounter.merge(ressource,producedValue,Integer::sum);
                    }
                }
            }
        }
    }

    public void sevenAtDice(Player playerTurn){ // TODO: 20/12/2021 compléter avec les appels de fonction de Vue pour terminer la fonction
        for(Player player:this.players){
            if((player.ressourceCount())>7){
                // appel de fonction de vue qui demande quelle ressource il doit défosser (quantité de ressource/2)
            }
        }

        // appel fonction de vue pour récupérer la case ou placer le voleur
        board.getThiefTile().setThief(false);
        // setThief la nouvelle Tile
        board.setThiefTile(null);
        // mettre la nouvelle tile en thief=true


        ArrayList<Colony> ownedColonies=new ArrayList<>();
        for(Colony colony:board.getThiefTile().getColonies()){
            if(colony.getPlayer()!=null){
                ownedColonies.add(colony);
            }
        }

        String ressource;
        Player playerOfColony;
        if(ownedColonies.size()==1){
            playerOfColony=ownedColonies.get(0).getPlayer();
            if(playerOfColony.ressourceCount()>0){
                do{
                    ressource=Board.generateRandomRessource();
                }
                while(playerOfColony.ressources.get(ressource)==0);
                int ressourceStock=playerOfColony.ressources.get(ressource);
                playerOfColony.ressources.replace(ressource,ressourceStock-1);
                playerTurn.ressources.merge(ressource,1,Integer::sum);
            }
        }else if(ownedColonies.size()>=1){
            // appel fonction de vue pour demander à quelle colonie le joueur veut-il voler une carte
        }
    }

    public static void main(String[] args){
        Game game=new Game();
        /*Board test=new Board();
        test.afficher();
        int u=0;
        int j=0;
        int x=0;
        for(Tile[] t : test.getTiles()) {
            System.out.println("x="+u+" :");
            u++;
            j=0;
            for(Tile t1 : t) {
                System.out.print("        ");
                System.out.println("y="+j+" :");
                j++;
                x=0;
                for(Road r : t1.getRoads()) {
                    System.out.print("            "+r.checked+"   Route "+x+" :");
                    System.out.println(r);
                    x++;
                }
            }
        }
         */
    }
}
