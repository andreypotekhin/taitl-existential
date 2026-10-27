package com.taitl.ex.logic.managed_indexes;

import com.taitl.ex.common.annotations.*;
import com.taitl.ex.common.creator.*;
import com.taitl.ex.common.helper.collections.*;
import com.taitl.ex.logic.managed_indexes.actions.*;
import com.taitl.ex.logic.managed_indexes.data.*;
import com.taitl.ex.logic.execution.actions.*;
import com.taitl.ex.logic.types.*;
import com.taitl.ex.logic.types.data.*;
import com.taitl.existential.*;
import com.taitl.existential.exceptions.*;
import com.taitl.existential.events.*;
import com.taitl.existential.events.access_events.*;
import com.taitl.existential.events.types.*;
import com.taitl.existential.indexes.*;
import com.taitl.existential.keys.*;
import com.taitl.existential.transactions.*;
import java.util.*;
import java.util.function.*;

/**
 * Coordinates one handle's managed definitions, accepted reports, and folded physical maintenance.
 * Subject routing is cached by explicit descriptor and runtime class, never by entity payload.
 * Single-valued keys are captured on acceptance to preserve replacement history; multi-valued
 * keys are validated together on access. User report-time stages run first, so denied candidates
 * do not enter the transaction's relationship indexes.
 */
public class IndexMaintenanceLogic
{
    @Logic
    private BindManagedIndexes bind = Creator.create(BindManagedIndexes.class);
    @Logic
    private CompileIndexMaintenancePlan compile = Creator.create(CompileIndexMaintenancePlan.class);
    @Logic
    private ResolveReportedTypes reportedTypes = Creator.create(ResolveReportedTypes.class);
    @Logic
    private TypeMatchingLogic matching = Creator.create(TypeMatchingLogic.class);
    @Logic
    private FoldIndexChanges fold = Creator.create(FoldIndexChanges.class);
    @Logic
    private PrepareManagedIndexes prepare = Creator.create(PrepareManagedIndexes.class);
    @Logic
    private DisposeManagedIndexes dispose = Creator.create(DisposeManagedIndexes.class);

    private final ManagedAccess access;
    private Map<IndexDefinition<?>, ManagedBinding> bindings = new IdentityHashMap<>();
    private IndexMaintenancePlan plan = emptyPlan();
    private final BoundedCache<List<?>, List<ManagedSide<Object, Object>>> routes = new BoundedCache<>(256);
    private boolean bound;
    private Consumer<RuntimeKey<?>> warn;

    public IndexMaintenanceLogic(String op, Runnable check, Consumer<Throwable> failed)
    {
        access = new ManagedAccess(op, check, failed);
    }

    public void bind(Tr tr, Existential ex)
    {
        if (!bound)
        {
            bindings = bind.call(tr, ex, access);
            warn = key -> ex.transactions().logic().evaluationLogic.warnUnresolvedGenerics(key, tr);
            plan = new IndexMaintenancePlan(compile.call(bindings.values(), false),
                    compile.call(bindings.values(), true));
            bound = true;
        }
    }

    @SuppressWarnings("unchecked")
    public <I> I index(IndexDefinition<I> definition)
    {
        access.check();
        ManagedBinding binding = bindings.get(definition);
        if (binding == null)
        {
            throw new IllegalArgumentException("Register the managed index definition in this context or transaction "
                    + "before begin(). See /Troubleshooting.md#managed-index-maintenance");
        }
        return (I) binding.view();
    }

    public void accept(RuntimeKey<?> key)
    {
        Event<?> event = key.event();
        boolean read = event instanceof Read<?> || event instanceof RL<?> || event instanceof Access<?>;
        if (plan.routes(read).isEmpty())
        {
            return;
        }
        Object before = null;
        Object after = key.entity();
        if (event instanceof BiEvent<?>)
        {
            before = ((BiEvent<?>) event).t0;
            after = ((BiEvent<?>) event).t1;
        }
        else if (event instanceof Delete<?>)
        {
            before = after;
            after = null;
        }
        else if (!(read || event instanceof Create<?> || event instanceof Update<?> || event instanceof Write<?>))
        {
            return;
        }
        try
        {
            List<ManagedSide<Object, Object>> oldSides =
                    before == null ? Collections.emptyList() : sides(key, before, read);
            List<ManagedSide<Object, Object>> newSides =
                    after == null ? Collections.emptyList() : sides(key, after, read);
            Set<ManagedSide<Object, Object>> all = Collections.newSetFromMap(new IdentityHashMap<>());
            all.addAll(oldSides);
            all.addAll(newSides);
            List<ManagedChange> changes = new ArrayList<>(all.size());
            for (ManagedSide<Object, Object> side : all)
            {
                changes.add(new ManagedChange(side, oldSides.contains(side) ? before : null,
                        newSides.contains(side) ? after : null));
            }
            fold.call(changes);
        }
        catch (RuntimeException | Error failure)
        {
            if (failure instanceof ExistentialException)
            {
                throw failure;
            }
            throw access.failure("event enrollment", failure);
        }
    }

    public void prepare()
    {
        if (!bindings.isEmpty())
        {
            access.check();
            prepare.call(bindings.values());
        }
    }

    public Collection<ManagedBinding> bindings()
    {
        return Collections.unmodifiableCollection(bindings.values());
    }

    public void close()
    {
        dispose.call(bindings.values());
        bindings.clear();
        plan = emptyPlan();
        routes.clear();
        matching.close();
        access.close();
        warn = null;
    }

    private List<ManagedSide<Object, Object>> sides(RuntimeKey<?> key, Object entity, boolean read)
    {
        List<?> signature = Arrays.asList(key.typeKey().toString(), key.typeKey().getClass(), entity.getClass(), read);
        return routes.resolve(signature, () -> resolve(key, entity, read));
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    private List<ManagedSide<Object, Object>> resolve(RuntimeKey<?> key, Object entity, boolean read)
    {
        warn.accept(key);
        RuntimeKey<?> subject = RuntimeKey.valueOf((Event) key.event(), (TypeKey) key.typeKey(), entity, false);
        Map<String, List<ManagedSide<Object, Object>>> source = plan.routes(read);
        Set<TypePattern> patterns = reportedTypes.call(subject, false);
        patterns.addAll(reportedTypes.call(subject, true));
        Set<ManagedSide<Object, Object>> selected = Collections.newSetFromMap(new IdentityHashMap<>());
        for (TypePattern pattern : patterns)
        {
            for (ManagedSide<Object, Object> side : source.getOrDefault(pattern.raw(), Collections.emptyList()))
            {
                if (matching.matches(new TypePattern(side.type().toString()), pattern))
                {
                    selected.add(side);
                }
            }
        }
        return new ArrayList<>(selected);
    }

    private IndexMaintenancePlan emptyPlan()
    {
        return new IndexMaintenancePlan(Collections.emptyMap(), Collections.emptyMap());
    }

}
