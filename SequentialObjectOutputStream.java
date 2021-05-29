

import java.io.*;
import java.util.*;
import java.lang.reflect.*;
import android.util.*;

public class SequentialObjectOutputStream extends DataOutputStream
implements ObjectOutput
{
    interface FieldGetAction
    {
        void get(Object obj, Field field) throws IllegalAccessException, IOException;
    }

    interface ArrayGetAction
    {
        void get(Object array, int Index) throws ArrayIndexOutOfBoundsException, IOException;       
    }

    public HashMap<Class, FieldGetAction> Primatives;
    public HashMap<Class, ArrayGetAction> ArrayPrimatives;

    public SequentialObjectOutputStream(OutputStream stream)
    {
        super(stream);

        Primatives = new HashMap<Class, FieldGetAction>();

        try
        {
            Primatives.put(boolean.class,
            new FieldGetAction()
            {
                public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                {
                    boolean x = field.getBoolean(obj);
                    writeBoolean(x);

                }
            });

            Primatives.put(byte.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        byte x = field.getByte(obj);
                        writeByte(x);

                    }
                });


            Primatives.put(short.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        short x = field.getShort(obj);
                        writeShort(x);

                    }
                });


            Primatives.put(int.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        int x = field.getInt(obj);
                        writeInt(x);

                    }
                });


            Primatives.put(long.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        long x = field.getLong(obj);
                        writeLong(x);

                    }
                });


            Primatives.put(char.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        char x = field.getChar(obj);
                        writeChar(x);

                    }
                });


            Primatives.put(float.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        float x = field.getFloat(obj);
                        writeFloat(x);

                    }
                });


            Primatives.put(double.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        double x = field.getDouble(obj);
                        writeDouble(x);
                    }
                });


            Primatives.put(String.class,
                new FieldGetAction()
                {
                    public void get(Object obj, Field field) throws IllegalAccessException, IOException 
                    {
                        String x = (String) field.get(obj);
                        writeUTF(x);

                    }
                });
        } catch(Exception e)
        {
            Log.e("SOb", Log.getStackTraceString(e));
        }



        ArrayPrimatives = new HashMap<Class, ArrayGetAction>();

        try
        {
            ArrayPrimatives.put(boolean.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        boolean x = Array.getBoolean(obj, index);
                        writeBoolean(x);

                    }
                });

            ArrayPrimatives.put(byte.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        byte x = Array.getByte(obj, index);
                        writeByte(x);

                    }
                });


            ArrayPrimatives.put(short.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        short x = Array.getShort(obj, index);
                        writeShort(x);

                    }
                });


            ArrayPrimatives.put(int.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        int x = Array.getInt(obj, index);
                        writeInt(x);

                    }
                });


            ArrayPrimatives.put(long.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        long x = Array.getLong(obj, index);
                        writeLong(x);

                    }
                });


            ArrayPrimatives.put(char.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        char x = Array.getChar(obj, index);
                        writeChar(x);

                    }
                });


            ArrayPrimatives.put(float.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        float x = Array.getFloat(obj, index);
                        writeFloat(x);

                    }
                });


            ArrayPrimatives.put(double.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        double x = Array.getDouble(obj, index);
                        writeDouble(x);
                    }
                });


            ArrayPrimatives.put(String.class,
                new ArrayGetAction()
                {
                    public void get(Object obj, int index) throws ArrayIndexOutOfBoundsException, IOException   
                    {
                        String x = (String) Array.get(obj, index);
                        writeUTF(x);

                    }
                });
        } catch(Exception e)
        {
            Log.e("SOb", Log.getStackTraceString(e));
        }

    }

    class State
    {
        public ArrayList<Object> OStack = new ArrayList<Object>();

        public long currentId = 1;

        public HashMap<Object, Long> References = new HashMap<Object, Long>();

    }

    public void writeObject(Object A) throws IOException, NotSerializableException
    {
        final State state = new State();

        state.OStack.add(0, A);

        LinkedList<Object> ForStack = new LinkedList<Object>();

        while (!(state.OStack.size() == 0))
        {
            Object Current = state.OStack.get(0);
            state.OStack.remove(0);

            if (((Serializable) Current) == null)
            {
                throw new NotSerializableException();
            }


            //Type C = Current.getClass();

            Class C = Current.getClass();

            Log.i("SOb", "placing #"+Long.toString(state.currentId)+" of "+C.getCanonicalName()+" on graph"); 
            state.References.put(Current, state.currentId);
            state.currentId++;

            ForStack.add(Current);

            if (C.isArray())
            {
                //Array array = (Array) Current;
                Class Ctype = C.getComponentType();

                if (ArrayPrimatives.keySet().contains(Ctype) == false)
                {
                    for (int I=0; I<Array.getLength(Current); I++)
                    {
                        Object o = Array.get(Current, I);

                        if ((o != null) && (state.References.keySet().contains(o) == false))
                        {
                            if (state.OStack.contains(o) == false) state.OStack.add(state.OStack.size(), o);
                        }

                    }
                }
            } else
            {
                for (Class Cur = C; Cur != null; Cur = Cur.getSuperclass())
                {

                    Field[] fields = Cur.getDeclaredFields();

                    for (Field f : fields)
                    {
                        if (Modifier.isStatic(f.getModifiers()))
                        {
                            continue;
                        }

                        f.setAccessible(true);

                        if (f.isAccessible() == false)
                        {
                        //  Log.i("SOb", "     isAccessible = false");
                            continue;
                        }

                        Class type = f.getType();
                        //Log.i("SOb", "     field \""+f.getName()+"\" of "+type.getCanonicalName());

                        if (Primatives.keySet().contains(type) == false)
                        {       
                            try
                            {
                                Object o = f.get(Current);

                                if ((o != null) && (state.References.keySet().contains(o) == false))
                                {
                                    if (state.OStack.contains(o) == false) state.OStack.add(state.OStack.size(), o);
                                }

                            } catch (IllegalAccessException e)
                            {
                                Log.e("SOb", Log.getStackTraceString(e));
                            }
                        }
                    }
                }
            }
        }

        writeLong(state.References.size());

        for (Object O : ForStack )
        {
            Serializable s = (Serializable) O;

        //  if (s != null)
            {
                Class cl = O.getClass();

                String name = cl.getName();

                writeUTF(name);

                if (cl.isArray())
                {
                    Class components = cl.getComponentType();

                    ArrayGetAction action;

                    //Array array = (Array) O;

                    if (ArrayPrimatives.keySet().contains(components))
                    {
                        action = ArrayPrimatives.get(components);
                    } else
                    {
                        action = new ArrayGetAction()
                        {
                            public void get(Object array, int index) throws ArrayIndexOutOfBoundsException, IOException     
                            {
                                Object O = Array.get(array, index);
                                if (O==null)  writeLong(0);
                                else writeLong(state.References.get(O));
                            }
                        };
                    }

                    int length = Array.getLength(O);

                    writeInt(length);

                    for (int I=0; I<length; I++)
                    {
                        action.get(O, I);
                    }

                } else
                {
                    for (Class Cur = cl; Cur != null; Cur = Cur.getSuperclass())
                    {
                        Field[] fields = Cur.getDeclaredFields();

                        for (Field F : fields)
                        {
                            Class FieldType = F.getType();

                            F.setAccessible(true);

                            if (F.isAccessible() && (Modifier.isStatic(FieldType.getModifiers())))
                            {
                                FieldGetAction action;

                                //Array array = (Array) O;

                                if (Primatives.keySet().contains(FieldType))
                                {
                                    action = Primatives.get(FieldType);
                                } else
                                {
                                    action = new FieldGetAction()
                                    {
                                        public void get(Object obj, Field index) throws IllegalAccessException, IOException     
                                        {
                                            Object O = index.get(obj);
                                            if (O==null)  writeLong(0);
                                            else writeLong(state.References.get(O));
                                        }
                                    };
                                }

                                try
                                {
                                    action.get(O, F);
                                } catch (IllegalAccessException e)
                                {
                                    Log.e("SOb", Log.getStackTraceString(e));
                                }

                            }
                        }

                    }
                }
            }   
        }
    }
}