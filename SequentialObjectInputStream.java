import java.io.*;
import java.util.*;
import java.lang.reflect.*;
//import android.util.*;

public class SequentialObjectInputStream extends DataInputStream implements ObjectInput
{
    interface FieldPutAction
    {
        void put(Object obj, Field field) throws IllegalAccessException, IOException;
    }

    interface ArrayPutAction
    {
        void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException;
    }

    public HashMap<Class, FieldPutAction> Primatives;
    public HashMap<Class, ArrayPutAction> ArrayPrimatives;

    public SequentialObjectInputStream(InputStream stream)
    {
        super(stream);

        Primatives = new HashMap<Class, FieldPutAction>();

        try
        {
            Primatives.put(boolean.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        boolean x = readBoolean(); 
                        field.setBoolean(obj, x);

                    }
                });

            Primatives.put(byte.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        byte x = readByte(); 
                        field.setByte(obj, x);

                    }
                });


            Primatives.put(short.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        short x = readShort(); 
                        field.setShort(obj, x);

                    }
                });


            Primatives.put(int.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        int x = readInt(); 
                        field.setInt(obj, x);

                    }
                });


            Primatives.put(long.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        long x = readLong(); 
                        field.setLong(obj, x);

                    }
                });


            Primatives.put(char.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        char x = readChar(); 
                        field.setChar(obj, x);

                    }
                });


            Primatives.put(float.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        float x = readFloat(); 
                        field.setFloat(obj, x);

                    }
                });


            Primatives.put(double.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        double x = readDouble(); 
                        field.setDouble(obj, x);

                    }
                });


            Primatives.put(String.class,
                new FieldPutAction()
                {
                    public void put(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        String x = readUTF(); 
                        field.set(obj, x);

                    }
                });
        } catch(Exception e)
        {
           // Log.e("SOb", Log.getStackTraceString(e));
        }

        ArrayPrimatives = new HashMap<Class, ArrayPutAction>();

        try
        {
            ArrayPrimatives.put(boolean.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        boolean x = readBoolean();
                        Array.setBoolean(obj, index, x);
                    }
                });

            ArrayPrimatives.put(byte.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        byte x = readByte(); 
                        Array.setByte(obj, index, x);

                    }
                });


            ArrayPrimatives.put(short.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        short x = readShort(); 
                        Array.setShort(obj, index, x);

                    }
                });


            ArrayPrimatives.put(int.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        int x = readInt(); 
                        Array.setInt(obj, index, x);

                    }
                });


            ArrayPrimatives.put(long.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        long x = readLong(); 
                        Array.setLong(obj, index, x);

                    }
                });


            ArrayPrimatives.put(char.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        char x = readChar(); 
                        Array.setChar(obj, index, x);

                    }
                });


            ArrayPrimatives.put(float.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        float x = readFloat(); 
                        Array.setFloat(obj, index, x);

                    }
                });


            ArrayPrimatives.put(double.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        double x = readDouble(); 
                        Array.setDouble(obj, index, x);

                    }
                });


            ArrayPrimatives.put(String.class,
                new ArrayPutAction()
                {
                    public void put(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        String x = readUTF(); 
                        Array.set(obj, index, x);

                    }
                });
        } catch(Exception e)
        {
         //   Log.e("SOb", Log.getStackTraceString(e));
        }
    }


    @Override
    public Object readObject() throws ClassNotFoundException, IOException
    {
        long Total = readLong();

        //Log.i("SOb", "readObject : " + Long.toString(Total) + " objects in graph");

        HashMap<Long, Object> References = new HashMap<Long, Object>();

        long currentId = 1;

        HashMap<Object, HashMap<Field, Long>> refCache =
            new HashMap<Object, HashMap<Field, Long>>();
        final HashMap<Object, HashMap<Integer, Long>> arefCache =
            new HashMap<Object, HashMap<Integer,Long>>();

        for (int I=0; I < Total; I++)
        {
            String Name = readUTF();
            Class C = Class.forName(Name);

          //  Log.i("SOb", "Object of "+C.getCanonicalName() +" on graph");

            int adim = 0;

            Object O = null;

            if (C.isArray())
            {
                Class ComponentType = C.getComponentType();

                int Size = readInt();

             //   Log.i("SOb", "array of "+ComponentType.getCanonicalName() + ", " + Long.toString(Size) + " elements");          
                O = Array.newInstance(ComponentType, Size);

                References.put(currentId, O);
                currentId++;

                ArrayPutAction action = null;

                if (ArrayPrimatives.keySet().contains(ComponentType))
                {
                    action = ArrayPrimatives.get(ComponentType);
                } else
                {
                    arefCache.put(O, new HashMap<Integer, Long>());

                    action = new ArrayPutAction()
                    {
                        public void put(Object O, int Index) throws ArrayIndexOutOfBoundsException , IOException
                        {
                            long Ref = readLong();

                            arefCache.get(O).put(Index, Ref);
                        }
                    };
                }

                for (int index=0; index< Size; index++)
                {
                    action.put(O,index);
                }

            } else
            {

            try
            {

                O = 
                    C.getConstructor(new Class[0]).newInstance(new Object[0]);
            } catch(InstantiationException e)
            {
             //   Log.e("SOb", Log.getStackTraceString(e));
            } catch(NoSuchMethodException e)
            {
            //    Log.e("SOb", Log.getStackTraceString(e));
            } catch(IllegalAccessException e)
            {
             //   Log.e("SOb", Log.getStackTraceString(e));
            } catch(InvocationTargetException e)
            {
            //    Log.e("SOb", Log.getStackTraceString(e));
            }

            References.put(currentId, O);
            currentId++;
            refCache.put(O, new HashMap<Field, Long>());

            for (Field F : C.getFields())
            {
                if (F.isAccessible())
                {
                    Class T = F.getType();

                    if (Primatives.containsKey(T))
                    {
                        try
                        {
                            Primatives.get(T).put(O, F);
                        } catch (IllegalAccessException e)
                        {

                        }
                    } else
                    {
                        refCache.get(O).put(F, readLong());
                    }
                }
            }

        }
        }
        for (long I=0; I < Total; I++)
        {

            Object O = References.get(I+1);

            Class C = O.getClass();

            //Log.i("SOb", "get reference "+Long.toString(I)+" "+C.getCanonicalName());


            if (C.isArray())
            {
                HashMap<Integer,Long> aref_table = arefCache.get(O);

                if (ArrayPrimatives.containsKey(C.getComponentType()) == false)
                {

                    int len = Array.getLength(O);

                    for (int index=0; index<len; index++)
                    {
                        long r = aref_table.get(index);
                        Object ref = r == 0 ? null : References.get(r);

                        Array.set(O, index, ref);   
                    }
                }

            } else
            {

            HashMap<Field, Long> ref_table = refCache.get(O);

            for (Field F : C.getFields())
            {
                if (F.isAccessible())
                {
                    Class T = F.getType();

                    if (Primatives.containsKey(T) == false)
                    {
                        try
                        {
                            long r = ref_table.get(F);
                            Object ref = r == 0 ? null : References.get(r);

                            F.set(O, ref);
                        } catch (IllegalAccessException e)
                        {
                         //   Log.e("SOb", Log.getStackTraceString(e));
                        }

                    }
                }
            }
            }

        }


        return References.get((Long) (long) 1);
    }

}
