import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { useTranslation } from '../i18n'
import { getActiveCampaigns } from '../api/campaignApi'
import '../styles/campaigns.css'

export function CampaignsPage() {
  const navigate = useNavigate()
  const { t } = useTranslation()
  const [campaigns, setCampaigns] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)

  useEffect(() => {
    const fetchCampaigns = async () => {
      try {
        setLoading(true)
        setError(null)
        const data = await getActiveCampaigns()
        setCampaigns(data)
      } catch (err) {
        setError(t('campaigns.loadingError') || 'Failed to load campaigns')
        console.error('Failed to load campaigns:', err)
      } finally {
        setLoading(false)
      }
    }

    fetchCampaigns()
  }, [t])

  if (loading) {
    return (
      <section className="campaigns-container">
        <div className="loading-spinner">{t('common.loading')}</div>
      </section>
    )
  }

  if (error) {
    return (
      <section className="campaigns-container">
        <div className="error-message">
          <p>{error}</p>
        </div>
      </section>
    )
  }

  return (
    <section className="campaigns-container">
      <div className="campaigns-header">
        <h1>{t('campaigns.title') || 'Active Campaigns'}</h1>
        <p className="campaigns-subtitle">
          {t('campaigns.subtitle') || 'Discover our latest promotions and featured products'}
        </p>
      </div>

      {campaigns.length === 0 ? (
        <div className="empty-state">
          <p>{t('campaigns.empty') || 'No active campaigns at the moment'}</p>
          <button
            className="btn-continue-shopping"
            onClick={() => navigate('/search')}
          >
            {t('campaigns.browseCatalog') || 'Browse Catalog'}
          </button>
        </div>
      ) : (
        <div className="campaigns-grid">
          {campaigns.map((campaign) => (
            <div key={campaign.id} className="campaign-card">
              <div className="campaign-content">
                <h3 className="campaign-name">{campaign.name}</h3>
                {campaign.description && (
                  <p className="campaign-description">{campaign.description}</p>
                )}
                <div className="campaign-dates">
                  <span className="date-label">{t('campaigns.startDate') || 'Start'}: </span>
                  <span className="date-value">
                    {new Date(campaign.startDate).toLocaleDateString()}
                  </span>
                  <span className="separator"> - </span>
                  <span className="date-label">{t('campaigns.endDate') || 'End'}: </span>
                  <span className="date-value">
                    {new Date(campaign.endDate).toLocaleDateString()}
                  </span>
                </div>
                <div className="campaign-status">
                  {campaign.currentlyActive ? (
                    <span className="status-badge active">
                      {t('campaigns.statusActive') || 'Active'}
                    </span>
                  ) : (
                    <span className="status-badge inactive">
                      {t('campaigns.statusInactive') || 'Inactive'}
                    </span>
                  )}
                </div>
              </div>
            </div>
          ))}
        </div>
      )}
    </section>
  )
}
