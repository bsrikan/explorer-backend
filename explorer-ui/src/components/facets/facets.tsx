import React, { useState } from 'react';
import { Icon } from 'antd';
import Facet from '../facet/facet';
import styles from './facets.module.scss';

const Facets = (props) => {

  const handleFacetClick = (name, vals) => {
    props.onFacetClick(name, vals);
  }

  let facets: any = [];

  if (props.data) {
    facets = Object.keys(props.data).map((k, i) =>
      <Facet
        name={k}
        data={props.data[k]}
        key={i}
        onFacetClick={handleFacetClick}
      />
    );
  }

  const [show, toggleShow] = useState(true);

  return (
    <div className={styles.facetsContainer}>
      <div className={styles.header}>
        <div className={styles.title}>{props.title}</div>
        <div className={styles.toggle} onClick={() => toggleShow(!show)}>
          <Icon style={{fontSize: '12px'}} type={(show) ? 'up' : 'down'} />
        </div>
      </div>
      <div style={{display: (show) ? 'block' : 'none'}}>
        {facets}
      </div>
    </div>
  )
}

export default Facets;