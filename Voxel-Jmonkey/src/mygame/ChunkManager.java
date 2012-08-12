/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.math.Vector3f;
import java.awt.Point;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledThreadPoolExecutor;

/**
 *
 * @author Thad
 */
public class ChunkManager {//Take player as a parameter
    //private Map<Integer, Map<Integer, Chunk>> chunkMap = new HashMap<Integer, <Integer, Chunk>>();
    private Map<Integer, HashMap<Integer, Chunk>> chunkMap = new HashMap<Integer, HashMap<Integer, Chunk>>();
    private byte chunkPointer = 0;
    private int chunks = 0;
    private Main game;
    private Player player;
    private long updateTime = 30000;
    long timeLastUpdated = System.currentTimeMillis() - updateTime - 100;
    protected float bsize = 1f;
    protected byte ChunkLengthX = 16;
    protected byte ChunkLengthZ = 16;
    
    private boolean chunkLoaded = false;
    private AssetManager assetManager;
    
    
    
    public ChunkManager(Main game){
        
        this.game = game;
        this.assetManager = game.getAssetManager();
    }

    
    public Chunk getChunk(int x, int z){
        if(isChunkLoaded(x, z)){
            return chunkMap.get(x).get(z);
        }else{
            loadChunk(x, z);
            return getChunk(x, z);
        }
    }
    
       public Chunk getChunk(Vector3f playerVector) {
        int chunkX = getChunkXat(playerVector.x);
        int chunkY = getChunkZat(playerVector.z);
        return getChunk(chunkX, chunkY);
    }
        
        
        private int getChunkXat(float x) {
        return (int)Math.round(((x-(ChunkLengthX/2))/bsize)/(float)ChunkLengthX);
    }

    private int getChunkZat(float z) {
       return (int)Math.round(((z-(ChunkLengthZ/2))/bsize)/(float)ChunkLengthZ);
    }
    

    public void loadChunk(int x, int z){
        
        
        
        
        
        
        
        long startTime = System.currentTimeMillis();
                chunks++;
                System.out.println("Loading Chunk: "+chunks+" At X:"+x+" Z:"+z);
                
                //NEED TO PUT THIS IN SEPARATE THREAD
             Chunk chunk = new Chunk(assetManager,x,z, this);
              long startTime2 = System.currentTimeMillis();
             CollisionShape sceneShape =
                CollisionShapeFactory.createMeshShape(chunk);
        RigidBodyControl rbc = new RigidBodyControl(sceneShape, 0);
        chunk.addControl(rbc);
         long endTime2 = System.currentTimeMillis();
               long totalTime2 = (endTime2-startTime2);
               System.out.println("Created Physics Shape in "+totalTime2+" ms.");
        
        //END NEEDS THREAD
        
         game.getRootNode().attachChild(chunk);
        game.getBulletAppState().getPhysicsSpace().add(rbc);
        
       
               long endTime = System.currentTimeMillis();
               long totalTime = (endTime-startTime);
               System.out.println("Loaded in "+totalTime+" ms.");
               
               if(!chunkMap.containsKey(x)){
               chunkMap.put(x, new HashMap<Integer, Chunk>());
               }
               chunkMap.get(x).put(z, chunk);
              
    }

    public void updateChunks(Vector3f pVector){
        //update chunks here
                int pChunkX = getChunkXat(pVector.getX());
        int pChunkZ = getChunkZat(pVector.getZ());
        
        for(int chunkMaxX = -3; chunkMaxX < 3; chunkMaxX++){
             for(int chunkMaxZ = -3; chunkMaxZ < 3; chunkMaxZ++){
        if(!isChunkLoaded(pChunkX+chunkMaxX, pChunkZ+chunkMaxZ)){
            System.out.println("Loading chunk");
loadChunk(pChunkX+chunkMaxX, pChunkZ+chunkMaxZ);
        }
    }
    }
        
    }
    
    void update() {
    
    }


    private boolean isChunkLoaded(int x, int z) {
        if(chunkMap.containsKey(x)){
  HashMap<Integer, Chunk> xMap = chunkMap.get(x);
  if(xMap.containsKey(z)){
      
      return true;
  }else{
      return false;
  }
}else{
            return false;
        }
    }

    
    
    
}
