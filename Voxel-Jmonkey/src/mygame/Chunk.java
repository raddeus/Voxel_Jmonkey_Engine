/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

//import java.util.Map;

import com.jme3.asset.AssetManager;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.math.Vector4f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import java.util.Random;

import com.jme3.scene.VertexBuffer.Type;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;
import jme3tools.converters.ImageToAwt;



/**
 *
 * @author nerminba
 */
public class Chunk extends Node
{	
    int chunkX, chunkY, chunkZ;
   // int startX, startY, startZ;
    byte lengthX;
    byte lengthY = 16;
    byte lengthZ;
    byte mapCells[][][];
    Mesh mapmesh = new Mesh();
	
    int rightFace = 0;
    int leftFace = 1;
    int topFace = 2;
    int bottomFace = 3;
    int frontFace = 4;
    int backFace = 5;
    int x,z;
    ArrayList<Vector3f> vertices = new ArrayList<Vector3f>(); // points
    ArrayList<Vector3f> normals = new ArrayList<Vector3f>(); // normals
    ArrayList<Vector2f> texCoord = new ArrayList<Vector2f>(); // tex cords
    ArrayList<Integer> indexes = new ArrayList<Integer>(); // indexes
	
	
	ArrayList<Vector4f> verticesColor = new ArrayList<Vector4f>();
	
    //String matDefName = "Common/MatDefs/Misc/ShowNormals.j3md";
    //String matDefName = "Common/MatDefs/Light/Lighting.j3md";
	String matDefName = "Common/MatDefs/Misc/Unshaded.j3md";

    float bsize = 1f; // block size

	
    // Texture coordinates
    Vector2f [] texCoord2 = new Vector2f[4];
	
	//
	AssetManager assetManager;
    private final ChunkManager chunkManager;
	public Chunk(AssetManager assetManager, int x, int z, ChunkManager chunkManager)
	{
		this.assetManager = assetManager;
                this.x = x;
                this.z = z;
                this.chunkManager = chunkManager;
                this.bsize = chunkManager.bsize;
                this.lengthX = chunkManager.ChunkLengthX;
                this.lengthZ = chunkManager.ChunkLengthZ;
                mapCells = new byte[lengthX][lengthY][lengthZ];
 long startTime = System.currentTimeMillis();
		loadMapData();
		addRandomCells();
		build();
                                               long endTime = System.currentTimeMillis();
               long totalTime = (endTime-startTime);
               System.out.println("Loaded Single Chunk in "+totalTime+" ms.");
               
	}

    Chunk(Chunk playerChunk) {
        this(playerChunk.assetManager, playerChunk.x, playerChunk.z, playerChunk.getChunkManager());
    }
	
	
    private void loadMapData()
	{
		for (int z = 0; z < lengthZ; z++) {
			for(int x = 0; x < lengthX; x++ ) {
				//for (int y = 0; y < (heightmap.getTrueHeightAtPoint(x, z)*0.08f); y++) {
				for (int y = 0; y < lengthY; y++) {
					/*
					System.out.print("x: "+x+" ");
					System.out.print("y: "+y+" ");
					System.out.print("z: "+z+"\n");
					*/
                    mapCells[x][y][z] = 1;
                }
            }
        }
    }
	
	public void cleanEdgeCells()
	{
		for (int x = 0; x < lengthX; x++) {
			for (int y = 0; y < lengthY; y++) {
				mapCells[x][y][0] = 0;
				mapCells[x][y][lengthZ-1] = 0;
				mapCells[x][2][1] = 0;
				mapCells[x][2][2] = 0;
			}
		}

		for (int z = 0; z < lengthZ; z++) {
			for (int y = 0; y < lengthY; y++) {
				mapCells[0][y][z] = 0;
				mapCells[lengthX-1][y][z] = 0;
			}
		}
		
		
	}
	
	private void addRandomCells()
	{
        Random r = new Random();
        byte blocktype;
		
        for (byte y = 1;  y < lengthY;  y++)	{
            for (byte z = 0;  z < lengthZ;  z++)	{
				for (byte x = 0;  x < lengthX;  x++)	{
                    blocktype = 1; // default
                    //if(y < 3) blocktype = 1; // base
                    if(mapCells[x][y][z] == 1)
					{
						if(r.nextInt(30) < 29) 
						{ 
							 blocktype = 0;
						}
						mapCells[x][y][z] = blocktype;
					}
					//System.out.print("mapCells["+x+"]["+y+"]["+z+"]:"+mapCells[x][y][z]+"\n");
				}
            }
        }
	}
	
	private void build()
	{
		for (byte z = 0;  z < lengthZ;  z++)	{
			for (byte x = 0;  x < lengthX;  x++)	{
				for (byte y = 0;  y < lengthY;  y++)	{
					if(mapCells[x][y][z] == 1)
					{
						createWalls(checkSix(x,y,z), x, y, z);
					}
				}
			}
		}
		
		Vector4f[] c4 = verticesColor.toArray(new Vector4f[verticesColor.size()]);
		
        int colorIndex = 0;
		float[] colorArray = new float[verticesColor.size()*4];
        //Set custom RGBA value for each Vertex. Values range from 0.0f to 1.0f
        for(int i = 0; i < verticesColor.size(); i++){
           // Red value (is increased by .2 on each next vertex here)
           colorArray[colorIndex++]= c4[i].x; //0.1f+(.2f*i);
           // Green value (is reduced by .2 on each next vertex)
           colorArray[colorIndex++]= c4[i].y; //0.9f-(0.2f*i);
           // Blue value (remains the same in our case)
           colorArray[colorIndex++]= c4[i].z;
           // Alpha value (no transparency set here)
           colorArray[colorIndex++]= c4[i].w;
        }
		
		
        Vector3f[] v3 = vertices.toArray(new Vector3f[vertices.size()]);
		/*Vector4f[] c4 = verticesColor.toArray(new Vector4f[verticesColor.size()]);*/
        //Vector3f[] n3 = normals.toArray(new Vector3f[normals.size()]);
        Vector2f[] v2 = texCoord.toArray(new Vector2f[texCoord.size()]);
        int indx[] = convertIntegers(indexes);

        mapmesh.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(v3));
       // mapmesh.setBuffer(Type.Normal, 3, BufferUtils.createFloatBuffer(n3));
        mapmesh.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(v2));
        mapmesh.setBuffer(Type.Index,    1, BufferUtils.createIntBuffer(indx));
		mapmesh.setBuffer(Type.Color, 4, colorArray);
		
		//System.out.print("test: "+c4[0].w);
		
        mapmesh.updateBound();
		
        // Creating a geometry, and apply a single color material to it
        Geometry levelGeom = new Geometry("OurMesh", mapmesh);
        //Material mat1 = new Material(assetManager, matDefName);
        Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        
        
       // Material mat1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //mat1.setColor("Color", ColorRGBA.Red);
        
		mat1.setTexture("ColorMap", assetManager.loadTexture("Textures/blocktexture.png"));
        mat1.setBoolean("VertexColor", true);
		
		levelGeom.setMaterial(mat1);
		this.attachChild(levelGeom);
	}
	
	
    private void createWalls(byte faces[], float x, float y, float z)
    {
    	//System.out.println("Creatin walls");
		int verticesSize = vertices.size();
		float halfbsize = bsize/2;
		float bx, by, bz;
                float bxNeg, bxPos, byNeg, byPos, bzNeg, bzPos;
		bx = (x * bsize)+(this.x*this.lengthX*bsize);
		by = y * bsize;
		bz = (z * bsize)+(this.z*this.lengthZ*bsize);
		bxNeg = bx-halfbsize;
                bxPos = bx+halfbsize;
                byNeg =by-halfbsize;
                byPos =by+halfbsize;
                bzNeg =bz-halfbsize;
                bzPos = bz+halfbsize;
//		for (int i = 0; i < faces.length; i++) {
//			//System.out.print("face["+i+"]: "+faces[i]+"\n");
//		}

		Vector3f pa = new Vector3f(bxNeg, byNeg, bzPos);
		Vector3f pb = new Vector3f(bxPos, byNeg, bzPos);
		Vector3f pc = new Vector3f(bxNeg, byPos, bzPos);
		Vector3f pd = new Vector3f(bxPos, byPos, bzPos);

		Vector3f pe = new Vector3f(bxNeg, byNeg, bzNeg);
		Vector3f pf = new Vector3f(bxPos, byNeg, bzNeg);
		Vector3f pg = new Vector3f(bxNeg, byPos, bzNeg);
		Vector3f ph = new Vector3f(bxPos, byPos, bzNeg);

//		Vector3f normalUp = new Vector3f(0, 1, 0);
//		Vector3f normalDown = new Vector3f(0, -1, 0);
//		Vector3f normalRight = new Vector3f(1, 0, 0);
//		Vector3f normalLeft = new Vector3f(-1, 0, 0);
//		Vector3f normalFront = new Vector3f(0, 0, 1);
//		Vector3f normalBack = new Vector3f(0, 0, -1);


                Vector2f t1 = new Vector2f(0, 0);
		Vector2f t2 = new Vector2f(1.0f, 0);
		Vector2f t3 = new Vector2f(0, 1.0f);
		Vector2f t4 = new Vector2f(1.0f, 1.0f);

		//Vector2f top1 = new Vector2f(0.5f, 0.5f);
		//Vector2f top2 = new Vector2f(1.0f, 0.5f);
		//Vector2f top3 = new Vector2f(0.5f, 1.0f);
		//Vector2f top4 = new Vector2f(1.0f, 1.0f);


		// x = +
		if(faces[rightFace] == 1)
		{
			vertices.add(pb);
			vertices.add(pf);
			vertices.add(pd);
			vertices.add(ph);
//			normals.add(normalRight);
//			normals.add(normalRight);
//			normals.add(normalRight);
//			normals.add(normalRight);
			texCoord.add(t1);
			texCoord.add(t2);
			texCoord.add(t3);
			texCoord.add(t4);
			indexes.add(verticesSize+2);
			indexes.add(verticesSize+0);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+3);
			indexes.add(verticesSize+2);
			
			//vertex colors
			verticesColor.add(new Vector4f(.5f, .5f, .5f, 1f));
			verticesColor.add(new Vector4f(.5f, .5f, .5f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
		}

		// x = -
		if(faces[leftFace] == 1)
		{
			verticesSize = vertices.size();

			vertices.add(pe);
			vertices.add(pa);
			vertices.add(pg);
			vertices.add(pc);
//			normals.add(normalLeft);
//			normals.add(normalLeft);
//			normals.add(normalLeft);
//			normals.add(normalLeft);
			texCoord.add(t1);
			texCoord.add(t2);
			texCoord.add(t3);
			texCoord.add(t4);
			indexes.add(verticesSize+2);
			indexes.add(verticesSize+0);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+3);
			indexes.add(verticesSize+2);
			
			verticesColor.add(new Vector4f(.5f, .5f, .5f, 1f));
			verticesColor.add(new Vector4f(.5f, .5f, .5f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
		}
		//y = +
		if(faces[topFace] == 1)
		{
			verticesSize = vertices.size();

			vertices.add(pc);
			vertices.add(pd);
			vertices.add(pg);
			vertices.add(ph);
			//normals.add(normalUp);
			//normals.add(normalUp);
			//normals.add(normalUp);
			//normals.add(normalUp);
			texCoord.add(t1);
			texCoord.add(t2);
			texCoord.add(t3);
			texCoord.add(t4);
			indexes.add(verticesSize+2);
			indexes.add(verticesSize+0);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+3);
			indexes.add(verticesSize+2);
			
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
		}
		//y = -
		if(faces[bottomFace] == 1)
		{
			verticesSize = vertices.size();
			
			vertices.add(pe);
			vertices.add(pf);
			vertices.add(pa);
			vertices.add(pb);
			//normals.add(normalDown);
//			normals.add(normalDown);
//			normals.add(normalDown);
//			normals.add(normalDown);
			texCoord.add(t1);
			texCoord.add(t2);
			texCoord.add(t3);
			texCoord.add(t4);
			indexes.add(verticesSize+2);
			indexes.add(verticesSize+0);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+3);
			indexes.add(verticesSize+2);
			
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
		}

		if(faces[frontFace] == 1)
		{
			verticesSize = vertices.size();
			
			vertices.add(pa);
			vertices.add(pb);
			vertices.add(pc);
			vertices.add(pd);
			//normals.add(normalFront);
//			normals.add(normalFront);
//			normals.add(normalFront);
//			normals.add(normalFront);
			texCoord.add(t1);
			texCoord.add(t2);
			texCoord.add(t3);
			texCoord.add(t4);
			indexes.add(verticesSize+2);
			indexes.add(verticesSize+0);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+3);
			indexes.add(verticesSize+2);
			
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
		}

		if(faces[backFace] == 1)
		{
			verticesSize = vertices.size();

			vertices.add(pf);
			vertices.add(pe);
			vertices.add(ph);
			vertices.add(pg);
			//normals.add(normalBack);
//			normals.add(normalBack);
//			normals.add(normalBack);
//			normals.add(normalBack);
			texCoord.add(t1);
			texCoord.add(t2);
			texCoord.add(t3);
			texCoord.add(t4);
			indexes.add(verticesSize+2);
			indexes.add(verticesSize+0);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+1);
			indexes.add(verticesSize+3);
			indexes.add(verticesSize+2);
			
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
			verticesColor.add(new Vector4f(.25f, .25f, .25f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
			verticesColor.add(new Vector4f(1f, 1f, 1f, 1f));
		}
	}
	
	private byte[] checkSix(byte x, byte y, byte z) 
	{
		//System.out.println("checkSix: " + x + ", " + y + ", " + z);

		byte faces[] = new byte[6];

		for (byte i = 0; i < faces.length; i++) {
			faces[i] = 1;
		}

		// right face
		if (indexExists(lengthX, (x+1)))
		{
			if(mapCells[x+1][y][z] == 1){ faces[0] = 0; }
		}
		//System.out.println("right face: "+faces[0]);

		// left face
		//int xD = x-1;
		if (indexExists(lengthX, x-1))
		{
			if(mapCells[x-1][y][z] == 1){ faces[1] = 0; }
		}
		//System.out.println("left face: "+faces[1]);

		// top face
		if (indexExists(lengthY, y+1))
		{
			if(mapCells[x][y+1][z] == 1){ faces[2] = 0; }
		}
		//System.out.println("top face: "+faces[2]);

		// bottom face
		if (indexExists(lengthY, y-1))
		{
			if(mapCells[x][y-1][z] == 1){ faces[3] = 0; }
		}
		//System.out.println("bottom face: "+faces[3]);
		// back face
		if (indexExists(lengthZ, z+1))
		{
			if(mapCells[x][y][z+1] == 1){ faces[4] = 0; }
		}
		//System.out.println("back face: "+faces[4]);
		// front face
		if (indexExists(lengthZ, z-1))
		{
			if(mapCells[x][y][z-1] == 1){ faces[5] = 0; }
		}
		//System.out.println("front face: "+faces[5]);
		return faces;
	}

	public boolean indexExists(int size, int index) 
	{
		boolean result = false;
		if (index >= 0 && index < size){
			result = true;
		}
		return result;
	}
	
	public static int[] convertIntegers(ArrayList<Integer> integers)
	{
		int[] ret = new int[integers.size()];
		for (int i=0; i < ret.length; i++)
		{
			ret[i] = integers.get(i).intValue();
		}
		return ret;
	}
    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Chunk)){
            return false;
        }else{
            Chunk otherChunk = (Chunk)o;
                    if ((this.x == otherChunk.x)&&(this.z == otherChunk.z)){
            return true;
        }else{
            return false;
        }
        }
    }

    /**
     * @return the chunkManager
     */
    public ChunkManager getChunkManager() {
        return chunkManager;
    }
    
}
