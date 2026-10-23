package com.taitl.ex.examples.night_city.data;

import com.taitl.ex.examples.night_city.model.*;
import com.taitl.ex.examples.night_city.model.material.*;
import com.taitl.ex.examples.night_city.model.taxonomy.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

public class CityWorkload
{
    public final List<Cat> cats;
    public final Map<Cat, Collection<Dwelling<Cat, ?>>> dwellings;
    public final List<Report> looks;
    public final int descriptors;

    public CityWorkload(int size)
    {
        this(size, 20);
    }

    public CityWorkload(int size, int typeCount)
    {
        if (size < 1)
        {
            throw new IllegalArgumentException("Workload size must be positive");
        }
        cats = new ArrayList<>(size);
        dwellings = new LinkedHashMap<>(size);
        List<LookFactory> factories = lookFactories();
        if (typeCount < 1 || typeCount > factories.size())
        {
            throw new IllegalArgumentException("Workload descriptor count must be between 1 and " + factories.size());
        }
        factories = new ArrayList<>(factories.subList(0, typeCount));
        descriptors = typeCount;
        looks = new ArrayList<>(size);
        for (int i = 0; i < size; i++)
        {
            String place = "Park";
            Cat cat = new Cat("Black", place);
            cats.add(cat);
            dwellings.put(cat, List.of(new Dwelling<Cat, Rugs>("Grey", place)));
            LookFactory factory = factories.get(i % factories.size());
            looks.add(new Report(factory.create(), factory.type));
        }
    }

    protected List<LookFactory> lookFactories()
    {
        List<LookFactory> factories = new ArrayList<>();
        add(factories, new TypeKey<Look<Cat>>() {
        },
                () -> new Look<>(new Cat("Black", "Park"), "city cat"));
        add(factories, new TypeKey<Look<Mouse>>() {
        },
                () -> new Look<>(new Mouse("Grey", "Park"), "city mouse"));
        add(factories, new TypeKey<Look<House>>() {
        },
                () -> new Look<>(new House("Red", new Address("Park 1")), "city house"));
        add(factories, new TypeKey<Look<Being<Felidae>>>() {
        },
                () -> new Look<Being<Felidae>>(new Cat("Black", "Park"), "feline"));
        add(factories, new TypeKey<Look<Being<Muridae>>>() {
        },
                () -> new Look<Being<Muridae>>(new Mouse("Grey", "Park"), "murid"));
        addBuilding(factories, Brick.class);
        addBuilding(factories, Cardboard.class);
        addBuilding(factories, Wood.class);
        addBuilding(factories, Rugs.class);
        addBuilding(factories, Steel.class);
        addBuildingProjections(factories, "Brick", () -> new Building<Brick>("Red"));
        addBuildingProjections(factories, "Cardboard", () -> new Building<Cardboard>("Pink"));
        addBuildingProjections(factories, "Wood", () -> new Building<Wood>("Brown"));
        addBuildingProjections(factories, "Rugs", () -> new Building<Rugs>("Blue"));
        addBuildingProjections(factories, "Steel", () -> new Building<Steel>("Silver"));
        addDwellings(factories, Cat.class);
        addDwellings(factories, Mouse.class);
        addDwellingProjections(factories, "Cat", "Brick", () -> new Dwelling<Cat, Brick>("Red", "Park"));
        addDwellingProjections(factories, "Cat", "Cardboard",
                () -> new Dwelling<Cat, Cardboard>("Pink", "Park"));
        addDwellingProjections(factories, "Cat", "Wood", () -> new Dwelling<Cat, Wood>("Brown", "Park"));
        addDwellingProjections(factories, "Cat", "Rugs", () -> new Dwelling<Cat, Rugs>("Blue", "Park"));
        addDwellingProjections(factories, "Cat", "Steel", () -> new Dwelling<Cat, Steel>("Silver", "Park"));
        addDwellingProjections(factories, "Mouse", "Brick", () -> new Dwelling<Mouse, Brick>("Red", "Park"));
        addDwellingProjections(factories, "Mouse", "Cardboard",
                () -> new Dwelling<Mouse, Cardboard>("Pink", "Park"));
        addDwellingProjections(factories, "Mouse", "Wood", () -> new Dwelling<Mouse, Wood>("Brown", "Park"));
        addDwellingProjections(factories, "Mouse", "Rugs", () -> new Dwelling<Mouse, Rugs>("Blue", "Park"));
        addDwellingProjections(factories, "Mouse", "Steel", () -> new Dwelling<Mouse, Steel>("Silver", "Park"));
        return factories;
    }

    protected void addBuildingProjections(List<LookFactory> factories, String material, Supplier<?> building)
    {
        addProjections(factories, "Building<" + material + ">", building,
                material.toLowerCase(Locale.ROOT) + " building");
    }

    protected void addDwellingProjections(List<LookFactory> factories, String resident, String material,
            Supplier<?> dwelling)
    {
        addProjections(factories, "Dwelling<" + resident + "," + material + ">", dwelling,
                resident.toLowerCase(Locale.ROOT) + " " + material.toLowerCase(Locale.ROOT) + " dwelling");
    }

    protected void addProjections(List<LookFactory> factories, String valueType, Supplier<?> value, String label)
    {
        for (String container : List.of("List", "Set", "Optional", "Map<String,", "ArrayList", "HashSet",
                "LinkedList", "Deque", "Queue", "Iterable", "Vector", "CopyOnWriteArrayList"))
        {
            boolean map = container.startsWith("Map");
            String rawContainer = map ? "Map" : container;
            String innerTypes = map ? "String," + valueType : valueType;
            String type = "Look<" + rawContainer + "<" + innerTypes + ">>";
            TypeKey<?> key = TypeKey.valueOf(type);
            factories.add(new LookFactory(key, () -> new Look<>(projection(container, value.get()), label)));
        }
    }

    protected Object projection(String container, Object value)
    {
        switch (container)
        {
        case "List":
            return List.of(value);
        case "Set":
            return Set.of(value);
        case "Optional":
            return Optional.of(value);
        case "Map<String,":
            return Map.of("Park", value);
        case "ArrayList":
            return new ArrayList<>(List.of(value));
        case "LinkedList":
        case "Deque":
        case "Queue":
            return new LinkedList<>(List.of(value));
        case "Iterable":
            return List.of(value);
        case "Vector":
            return new Vector<>(List.of(value));
        default:
            return new java.util.concurrent.CopyOnWriteArrayList<>(List.of(value));
        }
    }

    protected void addBuilding(List<LookFactory> factories, Class<? extends Material> material)
    {
        if (material == Brick.class)
        {
            add(factories, new TypeKey<Look<Building<Brick>>>() {
            },
                    () -> new Look<>(new Building<Brick>("Red"), "brick building"));
        }
        else if (material == Cardboard.class)
        {
            add(factories, new TypeKey<Look<Building<Cardboard>>>() {
            },
                    () -> new Look<>(new Building<Cardboard>("Pink"), "cardboard building"));
        }
        else if (material == Wood.class)
        {
            add(factories, new TypeKey<Look<Building<Wood>>>() {
            },
                    () -> new Look<>(new Building<Wood>("Brown"), "wood building"));
        }
        else if (material == Rugs.class)
        {
            add(factories, new TypeKey<Look<Building<Rugs>>>() {
            },
                    () -> new Look<>(new Building<Rugs>("Blue"), "rug building"));
        }
        else if (material == Steel.class)
        {
            add(factories, new TypeKey<Look<Building<Steel>>>() {
            },
                    () -> new Look<>(new Building<Steel>("Silver"), "steel building"));
        }
    }

    protected void addDwellings(List<LookFactory> factories, Class<?> resident)
    {
        if (resident == Cat.class)
        {
            addCatDwellings(factories);
        }
        else
        {
            addMouseDwellings(factories);
        }
    }

    protected void addCatDwellings(List<LookFactory> factories)
    {
        add(factories, new TypeKey<Look<Dwelling<Cat, Brick>>>() {
        },
                () -> new Look<>(new Dwelling<Cat, Brick>("Red", "Park"), "cat brick dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Cat, Cardboard>>>() {
        },
                () -> new Look<>(new Dwelling<Cat, Cardboard>("Pink", "Park"), "cat cardboard dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Cat, Wood>>>() {
        },
                () -> new Look<>(new Dwelling<Cat, Wood>("Brown", "Park"), "cat wood dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Cat, Rugs>>>() {
        },
                () -> new Look<>(new Dwelling<Cat, Rugs>("Blue", "Park"), "cat rug dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Cat, Steel>>>() {
        },
                () -> new Look<>(new Dwelling<Cat, Steel>("Silver", "Park"), "cat steel dwelling"));
    }

    protected void addMouseDwellings(List<LookFactory> factories)
    {
        add(factories, new TypeKey<Look<Dwelling<Mouse, Brick>>>() {
        },
                () -> new Look<>(new Dwelling<Mouse, Brick>("Red", "Park"), "mouse brick dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Mouse, Cardboard>>>() {
        },
                () -> new Look<>(new Dwelling<Mouse, Cardboard>("Pink", "Park"), "mouse cardboard dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Mouse, Wood>>>() {
        },
                () -> new Look<>(new Dwelling<Mouse, Wood>("Brown", "Park"), "mouse wood dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Mouse, Rugs>>>() {
        },
                () -> new Look<>(new Dwelling<Mouse, Rugs>("Blue", "Park"), "mouse rug dwelling"));
        add(factories, new TypeKey<Look<Dwelling<Mouse, Steel>>>() {
        },
                () -> new Look<>(new Dwelling<Mouse, Steel>("Silver", "Park"), "mouse steel dwelling"));
    }

    protected <T> void add(List<LookFactory> factories, TypeKey<Look<T>> type, Supplier<Look<T>> create)
    {
        factories.add(new LookFactory(type, create));
    }

    public static class Report
    {
        public final Look<?> entity;
        public final TypeKey<?> type;

        Report(Look<?> entity, TypeKey<?> type)
        {
            this.entity = entity;
            this.type = type;
        }
    }

    protected static class LookFactory
    {
        protected final TypeKey<?> type;
        protected final Supplier<? extends Look<?>> create;

        LookFactory(TypeKey<?> type, Supplier<? extends Look<?>> create)
        {
            this.type = type;
            this.create = create;
        }

        Look<?> create()
        {
            return create.get();
        }
    }

    public int size()
    {
        return cats.size();
    }
}
