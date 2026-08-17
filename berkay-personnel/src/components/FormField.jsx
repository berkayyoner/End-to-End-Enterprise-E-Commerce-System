export function FormField({ label, error, hint, children }) {
  return (
    <label className="form-field">
      <span className="form-field-label">{label}</span>
      {children}
      {error ? <span className="form-field-error">{error}</span> : hint ? <span className="form-field-hint">{hint}</span> : null}
    </label>
  )
}
