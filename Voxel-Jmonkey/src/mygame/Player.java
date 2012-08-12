/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.math.Vector3f;

/**
 *
 * @author Thad
 */
public class Player {
    private CharacterControl playerController;
    private Chunk lastPlayerChunk;
    private Chunk playerChunk;
    private Vector3f playerVector;
    private Vector3f lastPlayerVector;
    private Main game;
    private ChunkManager chunkManager;
    public Player(Main game, Vector3f pVector){//make vector3f
        this.game = game;
        this.chunkManager = game.getChunkManager();
        this.playerVector = pVector;
        this.playerChunk = chunkManager.getChunk(playerVector);
        chunkManager.updateChunks(playerVector);
       // this.lastPlayerChunk = new Chunk(playerChunk);
        CapsuleCollisionShape playerShape = new CapsuleCollisionShape(.3f, .75f, 1);
        playerController = new CharacterControl(playerShape, 0.1f);
        playerController.setJumpSpeed(7);
        playerController.setFallSpeed(30);
        playerController.setGravity(30);
        playerController.setPhysicsLocation(playerVector);
        
    }

    public void update(){
        
              //playerVector = playerController.getPhysicsLocation();
        if(lastPlayerVector==null){
            lastPlayerVector = playerVector;
        }

       
        
        BlockPosition newPos = getBlockPosition(playerVector);
        BlockPosition oldPos = getBlockPosition(lastPlayerVector);

        if (!newPos.equals(oldPos)){
onBlockChange();
        }else{

        }
       lastPlayerVector = playerVector.clone();
        
    }


    
    public BlockPosition getBlockPosition(Vector3f vector){
        //TODO need to add block size here.
        //System.out.println("Block pos X: "+(int)vector.x+"Y: "+(int)vector.y+"Z: "+(int)vector.z);
        return new BlockPosition(vector);
        
    }
    public CharacterControl getCharacterController() {
       return this.getPlayerController();
    }

    /**
     * @return the playerController
     */
    public CharacterControl getPlayerController() {
        return playerController;
    }

    /**
     * @param playerController the playerController to set
     */
    public void setPlayerController(CharacterControl playerController) {
        this.playerController = playerController;
    }

    /**
     * @return the playerVector
     */
    public Vector3f getPlayerVector() {
        return playerVector;
    }

    /**
     * @param playerVector the playerVector to set
     */
    public void setPlayerVector(Vector3f playerVector) {
        this.playerVector = playerVector;
    }

    private void onBlockChange() {
        //System.out.println("Block Change Called");
                lastPlayerChunk = new Chunk(playerChunk);
                //^ probably should just reassign vars.
               playerChunk = chunkManager.getChunk(playerVector); 
                if (!lastPlayerChunk.equals(playerChunk)){
                    onChunkChange();
                }
        
        //Calculate which chunks should be loaded and call the load/unload here.
        
        //Yeah this is wrong. Should be updated only when the player crosses the center lines/outer lines. With onChunkChange.
    }

    private void onChunkChange() {
     System.out.println("Chunk Change");
       chunkManager.updateChunks(playerVector);
    }



    
}
