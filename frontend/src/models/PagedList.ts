// asi forzamos a que las clases implementen ese metodo
import { type TResponse } from "$utils/apiFetch";
export interface FromJsonStatic<T> {
  fromJson(json: any): T;
}

export type PaginationLinks = {
  total?: number;
  //first?: string;
  //last?: string;
  //next?: string;
  //prev?: string;
};

export type PagedResult<T> = {
  items: T[];
  links: PaginationLinks;
};

export function isLastPage<T>(pageNum:number, pagedList :PagedResult<T> ) :boolean{
    return pageNum == pagedList.links.total
}

function parseLinkHeader(header: string | null): PaginationLinks {
  const links: PaginationLinks = {};
  if (!header) return links;

  // Ejemplo de header:
  // <http://.../api/businesses?page=2>; rel="next", <http://...>; rel="prev", <5>; rel="total"
  const parts = header.split(',');

  for (const part of parts) {
    const section = part.trim();

    const match = section.match(/<([^>]+)>\s*;\s*rel="([^"]+)"/);
    if (!match) continue;

    const [, url, rel] = match;

    switch (rel) {
      //case 'first':
      //  links.first = url;
      //  break;
      //case 'last':
      //  links.last = url;
      //  break;
      //case 'next':
      //  links.next = url;
      //  break;
      //case 'prev':
      //  links.prev = url;
      //  break;
      case 'total':
        const n = Number(url);
        if (!Number.isNaN(n)) {
          links.total = n;
        }
        break;
    }
  }

  return links;
}

export function parsePagedResponse<T>(
  response: TResponse,
  clazz: FromJsonStatic<T>
): PagedResult<T> {
  const json = response.body
    
  // el back te devuelve directamente un array
  const itemsRaw = Array.isArray(json) ? json : [];
  const items = itemsRaw.map((item) => clazz.fromJson({body:item}) );

  const linkHeader = response.headers.get('Link');
  const links = parseLinkHeader(linkHeader);

  return { items, links };
}
