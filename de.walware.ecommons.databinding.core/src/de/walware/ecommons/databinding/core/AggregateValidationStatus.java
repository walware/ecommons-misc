package de.walware.ecommons.databinding.core;

import java.util.Collection;

import org.eclipse.core.databinding.DataBindingContext;
import org.eclipse.core.databinding.ValidationStatusProvider;
import org.eclipse.core.databinding.observable.IObservableCollection;
import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.value.ComputedValue;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;


/**
 * This class can be used to aggregate status values from a data binding context
 * into a single status value. Instances of this class can be used as an
 * observable value with a value type of {@link IStatus}, or the static methods
 * can be called directly if an aggregated status result is only needed once.
 *
 * @since 1.0
 *
 */
public final class AggregateValidationStatus extends ComputedValue {
	
//	/**
//	 * Constant denoting an aggregation strategy that merges multiple non-OK
//	 * status objects in a {@link MultiStatus}. Returns an OK status result if
//	 * all statuses from the given validation status providers are the an OK
//	 * status. Returns a single status if there is only one non-OK status.
//	 *
//	 * @see #getStatusMerged(Collection)
//	 */
//	public static final int MERGED= 1;
	
	/**
	 * Constant denoting an aggregation strategy that always returns the most
	 * severe info status from the given validation status providers. If there is
	 * more than one status at the same severity level, it picks the first one
	 * it encounters.
	 *
	 * @see #getStatusMaxInfoSeverity(Collection)
	 */
	public static final int MAX_INFO_SEVERITY= 3;
	
	
	private final int strategy;
	private final IObservableCollection validationStatusProviders;
	
	
	/**
	 * Creates a new aggregate validation status observable for the given data
	 * binding context.
	 *
	 * @param dbc
	 *            a data binding context
	 * @param strategy
	 *            a strategy constant, one of {@link #MERGED} or
	 *            {@link #MAX_SEVERITY}.
	 * @since 1.1
	 */
	public AggregateValidationStatus(final DataBindingContext dbc, final int strategy) {
		this(dbc.getValidationRealm(), dbc.getValidationStatusProviders(),
				strategy);
	}
	
	/**
	 * @param validationStatusProviders
	 *            an observable collection containing elements of type
	 *            {@link ValidationStatusProvider}
	 * @param strategy
	 *            a strategy constant, one of {@link #MERGED} or
	 *            {@link #MAX_SEVERITY}.
	 * @see DataBindingContext#getValidationStatusProviders()
	 */
	public AggregateValidationStatus(
			final IObservableCollection validationStatusProviders, final int strategy) {
		this(Realm.getDefault(), validationStatusProviders, strategy);
	}

	/**
	 * @param realm
	 *            Realm
	 * @param validationStatusProviders
	 *            an observable collection containing elements of type
	 *            {@link ValidationStatusProvider}
	 * @param strategy
	 *            a strategy constant, one of {@link #MERGED} or
	 *            {@link #MAX_SEVERITY}.
	 * @see DataBindingContext#getValidationStatusProviders()
	 * @since 1.1
	 */
	public AggregateValidationStatus(final Realm realm,
			final IObservableCollection validationStatusProviders, final int strategy) {
		super(realm, IStatus.class);
		if (strategy != MAX_INFO_SEVERITY) {
			throw new IllegalArgumentException("strategy= " + strategy); //$NON-NLS-1$
		}
		this.validationStatusProviders= validationStatusProviders;
		this.strategy= strategy;
	}
	
	
	@Override
	protected Object calculate() {
		IStatus result;
//		if (strategy == MERGED) {
//			result= getStatusMerged(validationStatusProviders);
//		} else {
			result= getStatusMaxInfoSeverity(this.validationStatusProviders);
//		}
		return result;
	}
	
	/**
	 * Returns a status that always returns the most severe status from the
	 * given validation status providers. If there is more than one status at
	 * the same severity level, it picks the first one it encounters.
	 *
	 * @param validationStatusProviders
	 *            a collection of validation status providers
	 * @return a single status reflecting the most severe status from the given
	 *         validation status providers
	 */
	public static IStatus getStatusMaxInfoSeverity(
			final Collection<? extends ValidationStatusProvider> validationStatusProviders) {
		int maxSeverity= IStatus.OK;
		IStatus maxStatus= Status.OK_STATUS;
		for (final ValidationStatusProvider validationStatusProvider : validationStatusProviders) {
			final IStatus status= (IStatus) validationStatusProvider
					.getValidationStatus().getValue();
			final int severity= DataStatus.getInfoSeverity(status);
			if (severity > maxSeverity) {
				maxSeverity= severity;
				maxStatus= status;
			}
		}
		return maxStatus;
	}
	
}
