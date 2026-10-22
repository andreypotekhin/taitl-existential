package com.taitl.existential.builders;

import com.taitl.ex.common.creator.*;
import com.taitl.existential.configs.*;
import com.taitl.existential.constants.*;
import com.taitl.existential.constraints.*;
import com.taitl.existential.evaluables.*;
import com.taitl.existential.keys.*;

import java.util.*;
import java.util.function.*;

import static com.taitl.ex.common.helper.Args.*;
import static com.taitl.ex.common.helper.State.*;

/**
 * Builds a single Context - the rules that apply to a particular business operation,
 * for instance, 'api/users/create'.
 * Combines invariants, effects, intents, and optionally a transaction definition for the Context.
 *
 * @see ConfigBuilder
 */
public class ContextBuilder
{
    ConfigBuilder parent;
    String op;
    List<Supplier<? extends Evs<?>>> evsSuppliers;
    List<StageName> evsStages;
    Supplier<? extends Context> contextFactory;
    BiFunction<String, String, ? extends Transaction> transactionFactory;
    StageName stageCursor;
    boolean built;

    /**
     * Creates a context builder for a specific operation name.
     *
     * @param parentConfig
     *            Parent config builder
     * @param op
     *            Operation name to configure
     */
    public ContextBuilder(ConfigBuilder parentConfig, String op)
    {
        this.parent = parentConfig;
        this.op = op;
        this.evsSuppliers = new ArrayList<>();
        this.evsStages = new ArrayList<>();
    }

    /**
     * Starts building an {@link Invariant} for the given type key.
     *
     * Example:
     *   Ex.configure()
     *     .context("/app/docs/update")
     *        .invariant(new TypeKey<Account<Seller<Car>>>(){})
     *             .update(account -> account.valid());
     *        .invariant(new TypeKey<Document<JSON>>(){})
     *             .all(doc -> doc.valid());
     *
     * @param typeKey
     *            Type key for the invariant subject
     * @param <T>
     *            Subject type for the invariant
     * @return Invariant builder
     */
    public <T> InvariantBuilder<T> invariant(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        InvariantBuilder<T> ib = Creator.create(InvariantBuilder.class,
                new Class<?>[] { ContextBuilder.class, TypeKey.class },
                this,
                typeKey);
        register(() -> ib.build(), StageName.VALIDATION);
        return ib;
    }

    /**
     * Starts building an {@link Invariant} for the given subject type.
     *
     * Example:
     *   Ex.configure()
     *     .context("/app/docs/update")
     *        .invariant(Account.class)
     *             .update(account -> account.valid());
     *        .invariant(Document<JSON>.class) <-- Poor choice. Use TypeKey - see the Note below.
     *             .all(doc -> doc.valid());
     *
     * Note: This will not work well for generics - the underlying code
     * will not know exact generics class definition due to Java type erasure.
     * For generics, use the variant of this method which takes TypeKey anonymous
     * subclass as a parameter.
     * Example:
     *   .invariant(new TypeKey<Account<Seller<Car>>>(){})
     *
     * @param cls
     *            Subject type for the invariant
     * @param <T>
     *            Subject type for the invariant
     * @return Invariant builder
     */
    public <T> InvariantBuilder<T> invariant(Class<T> cls)
    {
        sane(cls, "cls");
        return invariant(Creator.create(TypeKey.class, new Class<?>[] { Class.class }, cls));
    }

    /**
     * Registers an already-built Invariant with this context.
     *
     * Example:
     *   Ex.configure()
     *     .context("/api/cats")
     *         .invariant(new Invariant<Cat>() {{
     *             create(c -> "Black".equals(c.color), "Cats are born black");
     *         }});
     *
     * @param invariant
     *            Invariant to register
     * @param <T>
     *            Subject type for the invariant
     * @return This builder for chaining
     */
    public <T> ContextBuilder invariant(Invariant<T> invariant)
    {
        sane(invariant, "invariant");
        register(() -> invariant, StageName.VALIDATION);
        return this;
    }

    /**
     * Starts building an {@link Effect} for the given type key.
     *
     * Example:
     *   Ex.configure()
     *     .context("/app/docs/update")
     *        .effect(new TypeKey<Effect<Document<HTML>>>(){})
     *             .write(doc -> doc.spellCheck())
     *        .effect(new TypeKey<Document<JSON>>(){})
     *             .write(doc -> doc.validate())
     *        .effect(new TypeKey<Document>(){})
     *             .write(doc -> doc.validate());
     *
     * @param typeKey
     *            Type key for the effect subject
     * @param <T>
     *            Subject type for the effect
     * @return Effect builder
     */
    public <T> EffectBuilder<T> effect(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        EffectBuilder<T> eb = Creator.create(EffectBuilder.class,
                new Class<?>[] { ContextBuilder.class, TypeKey.class },
                this,
                typeKey);
        register(() -> eb.build(), StageName.VALIDATION);
        return eb;
    }

    /**
     * Starts building an {@link Effect} for the given subject type.
     *
     * Example:
     *   Ex.configure()
     *     .context("/app/docs/update")
     *        .effect(Document.class)
     *             .write(doc -> doc.spellCheck())
     *        .effect(Document<JSON>>.class)          <-- Poor choice. Use TypeKey - see the Note below.
     *             .write(doc -> doc.validate())
     *        .effect(Document.class)
     *             .write(doc -> doc.validate());
     *
     * Note: This will not work well for generics - the underlying code
     * will not know exact generics class definition due to Java type erasure.
     * For generics, use the variant of this method which takes TypeKey anonymous
     * subclass as a parameter.
     * Example:
     *   .effect(new TypeKey<Account<Seller<Car>>>(){})
     *
     * @param cls
     *            Subject type for the effect
     * @param <T>
     *            Subject type for the effect
     * @return Effect builder
     */
    public <T> EffectBuilder<T> effect(Class<T> cls)
    {
        sane(cls, "cls");
        return effect(Creator.create(TypeKey.class, new Class<?>[] { Class.class }, cls));
    }

    /**
     * Registers an already-built Effect with this context.
     *
     * Example:
     *   Ex.configure()
     *     .context("/api/cats")
     *         .effect(new Effect<Cat>() {{
     *             create(c -> c.location = new Location("Park"), "Set location for all new cats");
     *         }});
     *
     * @param effect
     *            Effect to register
     * @param <T>
     *            Subject type for the effect
     * @return This builder for chaining
     */
    public <T> ContextBuilder effect(Effect<T> effect)
    {
        sane(effect, "effect");
        register(() -> effect, StageName.VALIDATION);
        return this;
    }

    /**
     * Starts building a sibling context on the parent builder.
     *
     * @param name
     *            Context name to build
     * @return Context builder for the named context
     */
    public ContextBuilder context(String name)
    {
        sane(name, "name");
        if (hasPendingConfiguration())
        {
            buildContext();
        }
        return parent.context(parent.requireContextNameMatchesParentContext(name, op));
    }

    /**
     * Starts building a sibling context with the same operation key.
     *
     * @return Context builder for this context operation key
     */
    public ContextBuilder context()
    {
        if (hasPendingConfiguration())
        {
            buildContext();
        }
        return parent.context(op);
    }

    /**
     * Starts building an {@link Intent} for the given type key.
     *
     * Example:
     *   Ex.configure()
     *     .context("/app/docs/update")
     *        .intent(new TypeKey<Account<Seller<Car>>>(){})
     *             .update()
     *        .intent(new TypeKey<Intent<Document<?>>>(){})
     *             .read()
     *             .write();
     *
     * @param typeKey
     *            Type key for the intent subject
     * @param <T>
     *            Subject type for the intent
     * @return Intent builder
     */
    public <T> IntentBuilder<T> intent(TypeKey<T> typeKey)
    {
        sane(typeKey, "typeKey");
        IntentBuilder<T> ib = Creator.create(IntentBuilder.class,
                new Class<?>[] { ContextBuilder.class, TypeKey.class },
                this,
                typeKey);
        register(() -> ib.build(), StageName.IMMEDIATE);
        return ib;
    }

    /**
     * Starts building an {@link Effect} for the given subject type.
     *
     * Example:
     *   Ex.configure()
     *     .context("/app/docs/update")
     *        .intent(Account.class)
     *             .update()
     *        .intent(Document<?>.class)         <-- Poor choice. Use TypeKey - see the Note below.
     *             .read()
     *             .write();
     *
     * Note: This will not work well for generics - the underlying code
     * will not know exact generics class definition due to Java type erasure.
     * For generics, use the variant of this method which takes TypeKey anonymous
     * subclass as a parameter.
     * Example:
     *   .intent(new TypeKey<Account<Seller<Car>>>(){})
     *
     * @param cls
     *            Subject type for the effect
     * @param <T>
     *            Subject type for the effect
     * @return Intent builder
     */
    public <T> IntentBuilder<T> intent(Class<T> cls)
    {
        sane(cls, "cls");
        return intent(Creator.create(TypeKey.class, new Class<?>[] { Class.class }, cls));
    }

    /**
     * Registers an already-built Intent with this context.
     *
     * Example:
     *   Ex.configure()
     *     .context("/api/cats")
     *         .intent(new Intent<Document<HTML>>() {{
     *             read();
     *             write();
     *         }}, new TypeKey<Document<HTML>>() {});
     *
     * @param intent
     *            Intent to register
     * @param typeKey
     *            Type key for the intent subject
     * @param <T>
     *            Subject type for the intent
     * @return This builder for chaining
     */
    public <T> ContextBuilder intent(Intent<T> intent, TypeKey<T> typeKey)
    {
        sane(intent, "intent", typeKey, "typeKey");
        intent.typeKey(typeKey);
        register(() -> intent, StageName.IMMEDIATE);
        return this;
    }

    /**
     * Sets begin as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder begin()
    {
        parent.verifyConfigurable();
        stageCursor = StageName.BEGIN;
        return this;
    }

    /**
     * Sets immediate as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder immediate()
    {
        parent.verifyConfigurable();
        stageCursor = StageName.IMMEDIATE;
        return this;
    }

    /**
     * Sets validation as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder validation()
    {
        parent.verifyConfigurable();
        stageCursor = StageName.VALIDATION;
        return this;
    }

    /**
     * Sets commit as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder commit()
    {
        parent.verifyConfigurable();
        stageCursor = StageName.COMMIT;
        return this;
    }

    /**
     * Sets checkpoint as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder checkpoint()
    {
        parent.verifyConfigurable();
        stageCursor = StageName.CHECKPOINT;
        return this;
    }

    /**
     * Sets rollback as the default stage for following rules.
     * @return
     *            This builder for chaining.
     */
    public ContextBuilder rollback()
    {
        parent.verifyConfigurable();
        stageCursor = StageName.ROLLBACK;
        return this;
    }

    void buildContext()
    {
        if (built)
        {
            return;
        }
        verify(!evsSuppliers.isEmpty() || transactionFactory != null,
                "Cannot configure context without defining rules");

        Context context = createInstance();
        context.op(op);

        for (int i = 0; i < evsSuppliers.size(); i++)
        {
            Evs<?> evs = evsSuppliers.get(i).get();
            StageName stageName = evsStages.get(i);
            if (evs instanceof Invariant<?> || evs instanceof Effect<?> || evs instanceof Intent<?>)
            {
                context.add(evs, stageName);
            }
            else
            {
                throw new IllegalStateException("Unexpected class in ruleSet: " + evs);
            }
        }

        if (transactionFactory != null)
        {
            context.transaction(transactionFactory);
        }
        parent.context(context);
        built = true;
    }

    /**
     * Starts building a transaction for the given operation name.
     *
     * @param name
     *            Operation name for the transaction
     * @return Transaction builder
     */
    public TransactionBuilder transaction(String name)
    {
        parent.verifyConfigurable();
        return Creator.create(TransactionBuilder.class,
                new Class<?>[] { ContextBuilder.class, String.class },
                this,
                name);
    }

    /**
     * Starts building a transaction using the supplied factory.
     *
     * @param supplier
     *            Factory for transaction instances
     * @return Transaction builder
     */
    public TransactionBuilder transaction(Supplier<? extends Transaction> supplier)
    {
        parent.verifyConfigurable();
        sane(supplier, "supplier");
        return Creator.create(TransactionBuilder.class,
                new Class<?>[] { ContextBuilder.class, Supplier.class },
                this,
                supplier);
    }

    /**
     * Starts configuring a transaction for this operation.
     *
     * @param factory
     *            Factory used to create the configured value.
     * @return
     *            This builder for chaining.
     */
    public TransactionBuilder transaction(BiFunction<String, String, ? extends Transaction> factory)
    {
        parent.verifyConfigurable();
        sane(factory, "factory");
        return Creator.create(TransactionBuilder.class,
                new Class<?>[] { ContextBuilder.class, BiFunction.class },
                this,
                factory);
    }

    Context createInstance()
    {
        if (contextFactory != null)
        {
            return contextFactory.get();
        }
        return parent.createContextInstance();
    }

    /**
     * Overrides the context factory used when building this context.
     *
     * @param supplier
     *            Context factory to use
     * @return This builder for chaining
     */
    public ContextBuilder contextFactory(Supplier<? extends Context> supplier)
    {
        parent.verifyConfigurable();
        sane(supplier, "supplier");
        contextFactory = supplier;
        return this;
    }

    Transaction createTransactionInstance(String op, String name)
    {
        return parent.createTransactionInstance(op, name);
    }

    protected void register(Supplier<? extends Evs<?>> supplier, StageName defaultStage)
    {
        parent.verifyConfigurable();
        sane(supplier, "supplier", defaultStage, "defaultStage");
        evsSuppliers.add(supplier);
        evsStages.add(stageCursor != null ? stageCursor : defaultStage);
    }

    protected boolean hasPendingConfiguration()
    {
        return !built && (!evsSuppliers.isEmpty() || transactionFactory != null);
    }
}
