import { useSearchParams } from 'react-router-dom'
import { useTranslation } from '../i18n'

export function SearchPage() {
  const { t } = useTranslation()
  const [searchParams] = useSearchParams()

  const mainCategoryId = searchParams.get('mainCategoryId')
  const subTypeId = searchParams.get('subTypeId')
  const innerTypeId = searchParams.get('innerTypeId')

  return (
    <section id="center">
      <div>
        <h1>{t('home.title')}</h1>
        <p>{t('search.placeholder')}</p>
        {mainCategoryId && <p>Main Category ID: {mainCategoryId}</p>}
        {subTypeId && <p>Sub Type ID: {subTypeId}</p>}
        {innerTypeId && <p>Inner Type ID: {innerTypeId}</p>}
      </div>
    </section>
  )
}
