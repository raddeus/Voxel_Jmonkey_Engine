package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * test
 * @author 
 */
public class Main extends SimpleApplication implements ActionListener {
    private BulletAppState bulletAppState;
    private RigidBodyControl landscape;
    private CharacterControl playerController;
    private Vector3f walkDirection = new Vector3f();
    private boolean left = false, right = false, up = false, down = false;
    private Vector3f sunLightDirection = new Vector3f(1.3f, -2.8f, -2.8f).normalizeLocal();
    DirectionalLight dl;

    public static void main(String[] args) {
        Main app = new Main();
        AppSettings newSetting = new AppSettings(true);
        newSetting.setFrameRate(30);
        app.setSettings(newSetting);
        app.start();
    }
    
    private Player player;
    private ChunkManager chunkManager;
    private boolean initialized = false;

    @Override
    public void simpleInitApp() {
    Logger.getLogger("").setLevel(Level.SEVERE);
        this.setDisplayStatView(false);
        setBulletAppState(new BulletAppState());
        stateManager.attach(getBulletAppState());
             setChunkManager(new ChunkManager(this));
        player = new Player(this, new Vector3f(0f, 60f, 0f));
        playerController = player.getCharacterController();
        
        getBulletAppState().getPhysicsSpace().add(playerController);
   
        
        setUpKeys();
        setUpLight();
        setUpSkybox();
        setUpCamera();
        //chunkManager.loadChunk(0, 0);
            this.initialized = true;
        
    }
//    private void addChunk(Chunk chunk) {
//       CollisionShape sceneShape =
//                CollisionShapeFactory.createMeshShape(chunk);
//        landscape = new RigidBodyControl(sceneShape, 0);
//        chunk.addControl(landscape);
//         rootNode.attachChild(chunk);
//         getBulletAppState().getPhysicsSpace().add(landscape);
//    }
    private void setUpLight() {

        dl = new DirectionalLight();
        dl.setColor(ColorRGBA.White.mult(.005f));
        dl.setDirection(sunLightDirection);
        rootNode.addLight(dl);
    }

    /** We over-write some navigational key mappings here, so we can
     * add physics-controlled walking and jumping: */
    private void setUpKeys() {
        inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, "Left");
        inputManager.addListener(this, "Right");
        inputManager.addListener(this, "Up");
        inputManager.addListener(this, "Down");
        inputManager.addListener(this, "Jump");
    }

    /** These are our custom actions triggered by key presses.
     * We do not walk yet, we just keep track of the direction the user pressed. */
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Left")) {
            left = value;
        } else if (binding.equals("Right")) {
            right = value;
        } else if (binding.equals("Up")) {
            up = value;
        } else if (binding.equals("Down")) {
            down = value;
        } else if (binding.equals("Jump")) {
            playerController.jump();
        }
    }

    /**
     * This is the main event loop--walking happens here.
     * We check in which direction the player is walking by interpreting
     * the camera direction forward (camDir) and to the side (camLeft).
     * The setWalkDirection() command is what lets a physics-controlled player walk.
     * We also make sure here that the camera moves with player.
     */
    @Override
    public void simpleUpdate(float tpf) {
        if (this.initialized){ //unnecessary?
        Vector3f camDir = cam.getDirection().clone().multLocal(0.1f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.075f);
        
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        //if (up)    { walkDirection.addLocal(camDir); }
        if (up) {
            walkDirection.addLocal(camDir.getX(), 0, camDir.getZ());
        }
        //if (down)  { walkDirection.addLocal(camDir.negate()); }
        if (down) {
            walkDirection.subtractLocal(camDir.getX(), 0, camDir.getZ());
        }
        playerController.setWalkDirection(walkDirection);
        cam.setLocation(playerController.getPhysicsLocation().addLocal(0, .75f, 0));
        player.setPlayerVector(cam.getLocation());
        player.update();
        }else{
            System.out.println("not updating because app isnt initialized");
            
        }
        
    }

    @Override
    public void simpleRender(RenderManager rm) {
        //TODO: add render code
    }

    private void setUpSkybox() {
        Texture west = assetManager.loadTexture("Textures/Skybox/left.png");
        Texture east = assetManager.loadTexture("Textures/Skybox/right.png");
        Texture north = assetManager.loadTexture("Textures/Skybox/front.png");
        Texture south = assetManager.loadTexture("Textures/Skybox/back.png");
        Texture top = assetManager.loadTexture("Textures/Skybox/top.png");
        Texture bot = assetManager.loadTexture("Textures/Skybox/bot.png");
        Spatial sky = SkyFactory.createSky(assetManager, west, east, north, south, top, bot);  
        rootNode.attachChild(sky);
    }

    private void setUpCamera() {
        cam.setFrustumPerspective(45f, (float) cam.getWidth() / cam.getHeight(), 0.01f, 1000f);
    }

//    private void loadChunks() {
//               int chunks = 0;
//        for(int x = 0; x<5; x++){
//            for(int y = 0; y<5; y++){
//                long startTime = System.currentTimeMillis();
//                chunks++;
//                System.out.println("Loading Chunk: "+chunks+"At X:"+x+" Y:"+y);
//             Chunk chunk = new Chunk(assetManager,x,y);
//               addChunk(chunk);
//               long endTime = System.currentTimeMillis();
//               long totalTime = (endTime-startTime);
//               System.out.println("Loaded in "+totalTime+" ms.");
//            }
//        }
//    }

    /**
     * @return the bulletAppState
     */
    public BulletAppState getBulletAppState() {
        return bulletAppState;
    }

    /**
     * @param bulletAppState the bulletAppState to set
     */
    public void setBulletAppState(BulletAppState bulletAppState) {
        this.bulletAppState = bulletAppState;
    }

    /**
     * @return the chunkManager
     */
    public ChunkManager getChunkManager() {
        return chunkManager;
    }

    /**
     * @param chunkManager the chunkManager to set
     */
    public void setChunkManager(ChunkManager chunkManager) {
        this.chunkManager = chunkManager;
    }


}
