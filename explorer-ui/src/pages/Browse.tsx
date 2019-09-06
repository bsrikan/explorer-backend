import React, { useState, useEffect } from 'react';
import axios from 'axios';
import Sidebar from '../components/sidebar/sidebar';
import SearchBar from '../components/search-bar/search-bar';
import SearchPagination from '../components/search-pagination/search-pagination';
import { Layout, Spin } from 'antd';
import SearchSummary from '../components/search-summary/search-summary';
import SearchResults from '../components/search-results/search-results';

const Browse: React.FC = () => {
  const { Content, Sider } = Layout;

  const [data, setData] = useState();
  const [facets, setFacets] = useState();
  const [searchUrl, setSearchUrl] = useState<any>({ url: `/datahub/v2/search?format=json&database=data-hub-FINAL`, method: 'post' });
  const [searchQuery, setSearchQuery] = useState();
  const [searchParams, setSearchParams] = useState({ start: 1, pageLength: 10 });
  const [searchFacets, setSearchFacets] = useState({});
  const [isLoading, setIsLoading] = useState(false);
  const [totalDocuments, setTotalDocuments] = useState();

  const getSearchResults = async () => {
    try {
      setIsLoading(true);
      const response = await axios({
        method: searchUrl.method,
        url: searchUrl.url,
        data: {
          query: searchQuery,
          entityNames: [], // TODO handle entity selection
          start: searchParams.start,
          pageLength: searchParams.pageLength,
          facets: searchFacets
        }
      });
      console.log('response.data', response.data);
      setData(response.data.results);
      setFacets(response.data.facets);
      setTotalDocuments(response.data.total);
      setIsLoading(false);
    } catch (error) {
      console.log('error', error.response);
    }
  }

  useEffect(() => {
    getSearchResults();
  }, [searchQuery, searchParams, searchFacets]);

  const handleSearch = (searchString: string) => {
    console.log('The user typed string is ' + searchString);
    setSearchQuery(searchString);
  }
  
  const handlePageChange = (pageNumber: number) => {
    console.log('The user selected page ' + pageNumber);
    setSearchParams({ ...searchParams, start: pageNumber});
  }

  const handlePageLengthChange = (current: number, pageSize: number) => {
    console.log('The user changed page length ' + pageSize);
    setSearchParams({ ...searchParams, pageLength: pageSize});
  }

  const handleFacetClick = (name, vals) => {
    console.log('Updated a facet ' + name + ': ' + vals);
    if (vals.length > 0) {
      setSearchFacets({ ...searchFacets, [name]: vals});
    } else {
      let newSearchFacets = { ...searchFacets };
      delete newSearchFacets[name];
      setSearchFacets(newSearchFacets);
    }
  }

  return (
    <Layout>
      <Sider width={300} style={{ background: '#f3f3f3' }}>
        <Sidebar 
          facets={facets} 
          onFacetClick={handleFacetClick}
        />
      </Sider>
      <Content style={{ background: '#fff', padding: '24px' }}>
        <SearchBar searchCallback={handleSearch} />
        {isLoading ? 
          <Spin tip="Loading..." style={{ margin: '100px auto', width: '100%'}} />
          : 
          <>
            <SearchSummary total={totalDocuments} start={searchParams.start} length={searchParams.pageLength} />
            <SearchPagination 
              total={totalDocuments} 
              onPageChange={handlePageChange} 
              onPageLengthChange={handlePageLengthChange} 
              currentPage={searchParams.start}
              pageLength={searchParams.pageLength} 
            />
            <br />
            <br />
            <SearchResults data={data} />
            <br />
            <SearchSummary total={totalDocuments} start={searchParams.start} length={searchParams.pageLength} />
            <SearchPagination 
              total={totalDocuments} 
              onPageChange={handlePageChange} 
              onPageLengthChange={handlePageLengthChange} 
              currentPage={searchParams.start}
              pageLength={searchParams.pageLength} 
            />
          </>
        }
      </Content>
    </Layout>
  );
}

export default Browse;